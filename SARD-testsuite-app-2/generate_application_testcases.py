"""
 This software was developed at the National Institute of Standards and
 Technology by employees of the Federal Government in the course of their
 official duties. Pursuant to title 17 Section 105 of the United States
 Code this software is not subject to copyright protection and is in the
 public domain. NIST assumes no responsibility whatsoever for its use by
 other parties, and makes no guarantees, expressed or implied, about its
 quality, reliability, or any other characteristic.

 We would appreciate acknowledgement if the software is used.
 The SAMATE project website is: https://samate.nist.gov

 This script is used to inflate SARD test cases that belong to applications.
 Such tests are sort of a patch to the original application, thus, instead of
 replicating an entire app throughout all its test cases, there will be only one app.

 This is done simply by:
 1. Create a "tmp" folder within a test case belonging to an application
 2. Move all test case files to "tmp" (patches)
 3. Copy the application.zip to test case folder an unzip it
 4. Move all files from "tmp" to test case root, patching the unzipped aplication
"""

import os, sys, glob, shutil, zipfile, sets

printDebug = False

#debug messages
def debug(str):
	global printDebug
	if printDebug:
		print(str)

# Unzip a file to a destination
def unzip(source_filename, dest_dir):
	debug("Extracting %s to %s" % (source_filename, dest_dir))
	with zipfile.ZipFile(source_filename) as zf:
		for member in zf.infolist():
			zf.extract(member, dest_dir)

# Transforms and id into directory path
# levels is how many levels of path:
# 2: 000/000
# 3: 000/000/000
#...
def idToPath(id, levels = 3):
	partial = id.rjust(levels * 3, '0')
	result = ""
	for i in range(1, levels + 1):
		index = i * 3
		result += partial[index - 3: index] + '/'
	return result[0: -1]

# Just print out some information so the user can take some action about it
def somethingWrong():
	print("** This is not an expected behavior **")
	print("Please make sure the manifest you've downloaded corresponds")
	print("to the current 'testcases' folder in this directory")
	print("and that you have read/write permissions in this file system")
	sys.exit(-1)

# Make sure a specific dir exists
def ensureExistence(directory):
	if len(directory) == 0:
		return True
	debug("Making sure '%s' exists..." % directory)
	if not os.path.isdir(directory):
		debug("Creating a new one")
		os.makedirs(directory, 0755)
		if not os.path.isdir(directory):
			somethingWrong()
			return False
	return True

# Merge src files into dst directory
def mergeDirs(root_src_dir, root_dst_dir):
	debug("Merging %s into %s" % (root_src_dir, root_dst_dir))
	for src_dir, dirs, files in os.walk(root_src_dir):
		dst_dir = src_dir.replace(os.sep + 'tmp', '')
		if not os.path.exists(dst_dir):
			os.mkdir(dst_dir)
		for file_ in files:
			src_file = os.path.join(src_dir, file_)
			dst_file = os.path.join(dst_dir, file_)
			if os.path.exists(dst_file):
				os.remove(dst_file)
                        debug(" '%s' -> '%s'" % (src_file, dst_dir))
			shutil.move(src_file, dst_dir)

# Returns true if it's still populating a test case, false otherwise
def populateTestcaseFiles(testcaseID, line):
	global testcaseFiles
	pos = line.find('</testcase')
	if pos > 0:
		return False

	pos = line.find('<file path="')
	if not (pos > 0):
		return True

	# Found it
	begin_id = pos + len('<file path="')
	last_quote = line.find('"', begin_id)
	tcPath = line[begin_id : last_quote]

	if not (testcaseID in testcaseFiles):
		testcaseFiles[testcaseID] = []

	testcaseFiles[testcaseID].append(tcPath)
	return True

apps = {}
testcaseFiles = {}
def populateApps(manifest):
	global apps
	lookingForFiles = False
	currentTestcaseID = -1
	with open(manifest, 'r') as f:
		for line in f:
			if lookingForFiles:
				lookingForFiles = populateTestcaseFiles(currentTestcaseID, line)
				continue

			pos = line.find('applicationid="')
			if not (pos > 0):
				continue

			# Get app_id
			begin_id = pos + len('applicationid="')
			last_quote = line.find('"', begin_id)
			app_id = line[begin_id : last_quote]

			if not (app_id in apps):
				apps[app_id] = []

			# Get test case id
			pos = line.find('testcase id="')
			if not (pos > 0):
				print("No test case was found for this app, please check the XML structure!")
				somethingWrong()
				return False

			begin_id = pos + len('testcase id="')
			last_quote = line.find('"', begin_id)
			currentTestcaseID = line[begin_id : last_quote]

			apps[app_id].append(currentTestcaseID)

			# Start looking for files next iteration
			lookingForFiles = True
	return True

# Copy files of test case to temporary folder
def cpTestcaseTmp(testcaseID):
	global testcaseFiles

	# Make sure this test case directory exsts
	idPath = idToPath(testcaseID)
	testcasePath = os.path.join("testcases", idPath) # testcases/###/###/###
	ensureExistence(testcasePath)

	# Make sure to work in a clean test case tmp
	tmpPath = os.path.join(testcasePath, "tmp") # testcases/###/###/###/tmp
	if os.path.isdir(tmpPath):
		shutil.rmtree(tmpPath)
	ensureExistence(tmpPath)

	emptyFolders = set()

	# Copy maniefst-only files to test case folder
	for testcaseFile in testcaseFiles[testcaseID]:
		fromPath = os.path.join("testcases", testcaseFile) # testcases/{SATE,shared/##,###/###/###}/somedir/somefile.file

		if "SATE" in testcaseFile: # SATE/somedir/somefile.file
			toPath = testcaseFile[testcaseFile.find('/', len("SATE/")) + 1:] # somefile.file
			toPath = os.path.join(tmpPath, toPath) # testcases/###/###/###/tmp/somefile.file
			ensureExistence(os.path.dirname(toPath)) # testcases/###/###/###/tmp/
			debug("Copying %s to %s" % (fromPath, toPath))
			shutil.copy(fromPath, toPath) # cp testcases/SATE/somedir/somefile.file testcases/###/###/###/tmp/somefile.file

		elif "shared" in testcaseFile: # shared/###/somedir/somefile.file
			toPath = testcaseFile[testcaseFile.find('/', len("shared/")) + 1:] # somedir/somefile.file
			toPath = os.path.join(tmpPath, toPath) # testcases/###/###/###/tmp/somedir/somefile.file
			ensureExistence(os.path.dirname(toPath)) # testcases/###/###/###/tmp/somedir/
			debug("Copying %s to %s" % (fromPath, toPath))
			shutil.copy(fromPath, toPath) # cp testcases/SATE/somedir/somefile.file testcases/###/###/###/tmp/somedir/somefile.file

		else: # ###/###/###/somedir/somefile.file
			# Move it from test case dir to tmp
			toPath = testcaseFile.replace(idPath + '/', "") # somedir/somefile.file
			toPath = os.path.join(tmpPath, toPath) # testcases/###/###/###/tmp/somedir/somefile.file
			ensureExistence(os.path.dirname(toPath)) # testcases/###/###/###/tmp/somedir/
			debug("Moving %s to %s" % (fromPath, toPath))
			shutil.move(fromPath, toPath) # mv testcases/###/###/###/somedir/somefile.file testcases/###/###/###/tmp/somedir/somefile.file

			# Add base folder of this file to be removed later
			emptyFolders.add(fromPath[:fromPath.find('/', len(testcasePath + '/')) + 1])

		for f in emptyFolders:
			debug("Deleting %s" % f)

	return testcasePath, tmpPath

# Does the actual test case building process
def inflateTestcases():
	# Check for testcases and app directory
	if not (ensureExistence("testcases") and ensureExistence(os.path.join("testcases", "app"))):
		return False

	# Check for the manifest files
	manifests = glob.glob('manifest*.xml')
	if len(manifests) == 0:
		debug("Ops! No manifest found! 'manifest*.xml'!")
		somethingWrong()

	# Look in the manifest for test cases that has application in it
	for m in manifests:
		if not populateApps(m):
			debug("Could not populate all applications. Please see messages above or contact our team for more information.")
			return False

	global apps
	for app in apps:

		# Make sure that app dir exist!
		appPath = os.path.join("testcases", "app", idToPath(app, 2))
		ensureExistence(appPath)
		appFiles = glob.glob(os.path.join(appPath, '*'))

		for testcaseID in apps[app]:
			debug("Inflating test case  %s..." % testcaseID)

			# Place test case files in "tmp"
			testcasePath, tmpPath = cpTestcaseTmp(testcaseID)

			# Copy the entire application to the test case dir
			debug("Bringing in application files")
			for appFile in appFiles:
				debug("\t - %s" % appFile)

				# Unzip it if '.zip'
				if ".zip" in appFile:
					unzip(appFile, testcasePath)

				# Just copy if otherwise
				else:
					shutil.copy(appFile, testcasePath)

			# In here, Testcase files within "tmp" will overwrite App ones
			mergeDirs(tmpPath, testcasePath)

			# For last, remove tmp directory
			shutil.rmtree(tmpPath)

	return True

if len(sys.argv) > 3 or (len(sys.argv) == 2 and sys.argv[1] != '-v'):
	print("Usage: %s [-v]" % sys.argv[0])
	print("	-v verbose mode, printing out more information on screen")
	sys.exit(-1)

printDebug = (len(sys.argv) == 2)

# Run it
if inflateTestcases():
	print("Done!")
else:
	print("Could not finish inflating all test cases that contain application!")
