# Directory
Directory &amp; File Chooser dialog for Android 3.0+ (API Level: 11+, "HONEYCOMB"+).

# Usage
1. Import module "Directory" into your project.
2. Add to gradle: `compile project(path: ':directory')`.
3. Add Java code:
```java
DirectoryProperties properties = new DirectoryProperties();
properties.setTitle("Select file");
properties.setButtonValueCancel("Cancel");
properties.setType(DirectoryProperties.OPEN_TYPE.FILE);
properties.setMode(DirectoryProperties.OPEN_MODE.MULTIPLE);
/* Customization
 *	properties.setDirectoryIconColor(getResources().getColor(R.color.colorPrimaryDark));
 *	properties.setFileIconColor(getResources().getColor(R.color.colorAccent));
 *	properties.setButtonIconColor(getResources().getColor(R.color.colorPrimary));
*/
Directory directory = new Directory(this, properties);
directory.setOnSuccessDialogListener(new Directory.OnSuccessDialogListener() {
	@Override
  public void onSuccessDialog(List<File> files) {
		// do
	}
);
```

# Credits
* Developed by hedhyw (http://hedhyw.ru)
* Icons by https://design.google.com/icons/

# License
Copyright (C) 2016 by hedhyw

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
