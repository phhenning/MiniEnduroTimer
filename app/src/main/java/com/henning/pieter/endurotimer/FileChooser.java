package com.henning.pieter.endurotimer;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;


public class FileChooser extends ListActivity {
	private File currentDir;
	private FileArrayAdapter adapter;
	private FileFilter fileFilter;
	private File fileSelected;
	private ArrayList<String> extensions;
	String pathOffset = "EnduroTimer/";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		Intent intent = getIntent();
		if (extras != null) {
			if (extras.getStringArrayList("filterFileExtension") != null) {
				extensions = extras.getStringArrayList("filterFileExtension");				
				fileFilter = new FileFilter() {
					@Override
					public boolean accept(File pathname) {						
						return ((pathname.isDirectory()) || (pathname.getName().contains(".")?extensions.contains(pathname.getName().substring(pathname.getName().lastIndexOf("."))):false));
					}
				};
			}
			if (intent.getStringExtra("filterFileStartPath") != null) {
				pathOffset = pathOffset.concat(intent.getStringExtra("filterFileStartPath"));
			}
		}
		// API 19 currentDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), pathOffset);
		currentDir = new File("/mnt/sdcard/Documents/" + pathOffset);
		fill(currentDir);
	}
	
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//        	if ((!currentDir.getName().equals("sdcard")) && (currentDir.getParentFile() != null)) {
//	        	currentDir = currentDir.getParentFile();
//	        	fill(currentDir);
//        	} else {
//        		finish();
//        	}
//            return false;
//        }
//        return super.onKeyDown(keyCode, event);
//	}

	private void fill(File f) {
		File[] dirs = null;
		if (fileFilter != null)
			dirs = f.listFiles(fileFilter);
		else 
			dirs = f.listFiles();
			
//		this.setTitle(getString(R.string.currentDir) + ": " + f.getName());
		List<Option> dir = new ArrayList<Option>();
		List<Option> fls = new ArrayList<Option>();
		try {
			for (File ff : dirs) {
				if (ff.isDirectory() && !ff.isHidden()) {
//					dir.add(new Option(ff.getName(), getString(R.string.folder), ff
//							.getAbsolutePath(), true, false));
					dir.add(new Option(ff.getName(), "", ff.getAbsolutePath(), true, false));
				} else {
					if (!ff.isHidden())
						fls.add(new Option(ff.getName(), "Size" + ": " + ff.length(), ff.getAbsolutePath(), false, false));
//					fls.add(new Option(ff.getName(), getString(R.string.fileSize) + ": "
//							+ ff.length(), ff.getAbsolutePath(), false, false));
				}
			}
		} catch (Exception e) {

		}
		Collections.sort(dir);
		Collections.sort(fls);
		dir.addAll(fls);
		if (!f.getName().equalsIgnoreCase("sdcard")) {
//			if (f.getParentFile() != null) dir.add(0, new Option("..", getString(R.string.parentDirectory), f.getParent(), false, true));
			if (f.getParentFile() != null) dir.add(0, new Option("..", "Up One Folder", f.getParent(), false, true));
		}
		adapter = new FileArrayAdapter(FileChooser.this, R.layout.file_view, dir);
		this.setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Option o = adapter.getItem(position);
		if (o.isFolder() || o.isParent()) {			
			currentDir = new File(o.getPath());
			fill(currentDir);
		} else {
			//onFileClick(o);
			fileSelected = new File(o.getPath());
			Intent intent = new Intent();
			intent.putExtra("fileSelected", fileSelected.getAbsolutePath());
			intent.putExtra("fileName", fileSelected.getName());
			intent.putExtra("folderName", fileSelected.getParent());
			setResult(Activity.RESULT_OK, intent);
			finish();
		}		
	}

	@Override
	public void onBackPressed() {
		System.out.println(" GO BAck ");
		finish();
	}
}