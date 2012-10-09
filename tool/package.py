# -*- coding: utf-8 -*-

import os
import shutil
import zipfile
import xml.sax as sax
from xml.sax.handler import ContentHandler

class MyHandler(ContentHandler) :
	def __init__(self, root='root'):
		self.root = root
	def startElement(self, name, attr) :
		if name == 'mod':
			if os.path.exists('package_temp'):
				shutil.rmtree('package_temp')
			self.modName = attr.getValue('name')
			self.version = attr.getValue('version')
		if name == 'file':
			fileFrom = os.path.abspath(attr.getValue('from'))
			fileName = os.path.split(fileFrom)[1]
			fileTo = os.path.abspath(os.path.join('package_temp', attr.getValue('to'), fileName))
			toDir = os.path.split(fileTo)[0]
			if not os.path.exists(toDir) :
				os.makedirs(toDir)
			shutil.copy(fileFrom, fileTo)
			if fileName == 'mcmod.info':
				f = open(fileTo, 'r')
				lines = f.read()
				f.close()
				lines = lines.replace('@VERSION@', self.version)
				f = open(fileTo, 'w')
				f.write(lines)
				f.close()
	def endElement(self, name):
		if name == 'mod' :
			zipName = self.modName + '_ver' + self.version + '.zip'
			print 'zip ' + zipName
			if not os.path.exists('package_out'):
				os.mkdir('package_out')
			zipf = zipfile.ZipFile(os.path.abspath(os.path.join('package_out', zipName)), 'w', zipfile.ZIP_DEFLATED)
			olddir = os.getcwd()
			os.chdir('package_temp')
			for root, dirs, files in os.walk('./'):
				for file in files:
					writeFile = os.path.join(root, file)
					print '\twriteFile:',writeFile
					try:
						zipf.write(writeFile, writeFile)
					except:
						print "ERROR: writing file =", writeFile
			zipf.close()
			os.chdir(olddir)
	def characters(self, char) :
		pass

if __name__ == '__main__' :
	parser = sax.make_parser()
	parser.setFeature(sax.handler.feature_namespaces, 0)
	parser.setContentHandler(MyHandler())
	dir = os.getcwd()
	os.chdir('../')
	file = file(os.path.join(dir,'package_config.xml'), 'r')
	try:
		parser.parse(file)
	finally:
		file.close()
