# Eulerity-Photos
Asynchronously loading images, appling edits to them and uploading them to a server. My first ever Jetpack Compose app, I learned a lot!

The following functionality is completed:

- [x] **Display a list of images**
  - [x] Made a GET request to /image to retrieve an array of JSON objects with “url” attribute pointing to a target image
  - [x] Images are loaded asynchronously
  - [x] Integrated LazyVerticalStaggeredGrid Composable API allowing to present photos that have a range of heights / widths

- [x] **Allow editing of an image**
  - [x] User can apply a color filter to the image
  - [x] User can to overlay text to the image
  - [x] User can apply and adjust blur effect to the image (Only works in Android 12 and above)

- [x] **Save the image**
  - [x] Made a GET request to /upload which returns a JSON object with a "url" attribute to which the image can be uploaded.
  - [x] Uploaded the image to the server using a POST request with a multipart/form-data encoding type

Ways to improve:
* Cache the images to avoid hitting the server needlessly
* Expensive operations can be optimized (see [EditScreen.kt](https://github.com/andreyyy178/Eulerity-Photos/blob/master/app/src/main/java/com/example/eulerityphotos/ui/screens/EditScreen.kt#L122)) 

## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='https://github.com/andreyyy178/Twitter/blob/master/walkthrough2.gif?raw=true' title='Video Walkthrough' width='' alt='Video Walkthrough' />
