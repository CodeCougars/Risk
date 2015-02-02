#!/usr/bin/env python
# -*- coding: <utf-8> -*-
# Author: Chris Mohler <cr33dog@gmail.com>
# Copyright 2009 Chris Mohler
# "Only Visible" and filename formatting introduced by mh
# Thanks to Michael Holzt for layer group code!
# License: GPL v3+
# Version 0.6
# GIMP compatibilty 2.4.x -> 2.8.x
# GIMP plugin to export layers as PNGs

from gimpfu import *
import os, re

gettext.install("gimp20-python", gimp.locale_directory, unicode=True)

def format_filename(img, layer):
	imgname = img.name.decode('utf-8')
	layername = layer.name.decode('utf-8')
	regex = re.compile("[^-\w]", re.UNICODE) 
        filename = layername + '.png' ## changed
	return filename

def get_layers(layers, only_visible):
	version = gimp.version[0:2]
	result = []
	for layer in layers:
		if version[0] >= 2 and version[1] >= 8: #version 2.8 and up
			if pdb.gimp_item_is_group(layer):
				result += get_layers(layer.children, only_visible)
			else:
				if only_visible:
					if layer.visible:
						layer.visible = 0
						result.append(layer)
				else:
					layer.visible = 0
					result.append(layer)
		else: #version below 2.8
			if only_visible:
				if layer.visible:
					layer.visible = 0
					result.append(layer)
			else:
				layer.visible = 0
				result.append(layer)
			
	return result


def export_layers(img, drw, path, only_visible=True, flatten=False, remove_offsets=False, crop=False):
	dupe = img.duplicate()
	layers = get_layers(dupe.layers, only_visible)

        layersStr = ""
	for layer in layers:
                x_off, y_off = layer.offsets
                name = layer.name.decode('utf-8')

                layersStr += "{\n"
                layersStr += "\"name\":" + "\"" + name + "\",\n"
                layersStr += "\"" + "pos" + "\": [" + str(x_off) + ", " + str(y_off) + "]\n"
                layersStr += "},\n"

		layer.visible = 1
		filename = format_filename(img, layer)
		fullpath = os.path.join(path, filename);
		tmp = dupe.duplicate()
		tmp.merge_visible_layers(0)
		if (flatten):
			tmp.flatten()
		if (remove_offsets):
			tmp.layers[0].set_offsets(0, 0) 
		if (crop):
			pdb.plug_in_zealouscrop(tmp, tmp.layers[0])
		pdb.file_png_save(tmp, tmp.layers[0], fullpath, filename, 0, 9, 1, 1, 1, 1, 1)
		dupe.remove_layer(layer)

        json = open(os.path.join(path, "regions.json"), "w")
        json.write(layersStr)
			
register(
	proc_name=("python-fu-risk"),
	blurb=("Risk export"),
	help=("Risk export"),
	author=("Chris Mohler <cr33dog@gmail.com>"),
	copyright=("Chris Mohler"),
	date=("2009"),
	label=("as _PNG"),
	imagetypes=("*"),
	params=[
		(PF_IMAGE, "img", "Image", None),
		(PF_DRAWABLE, "drw", "Drawable", None),
		(PF_DIRNAME, "path", "Save PNGs here", os.getcwd()),
		(PF_BOOL, "only_visible", "Only Visible Layers?", True),
		(PF_BOOL, "flatten", "Flatten Images?", False),
		(PF_BOOL, "remove_offsets", "Remove Offsets?", False),
		(PF_BOOL, "crop", "Zealous Crop", False),
		],
	results=[],
	function=(export_layers), 
	menu=("<Image>/File/E_xport Layers"), 
	domain=("gimp20-python", gimp.locale_directory)
	)

main()
