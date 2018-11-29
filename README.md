# Smile
 Capture your beautiful smiles and view them in an aesthetically pleasing mosaic view with Smile, it captures and saves your image when you smile. Smile can detect more than one smile, so gather your friends and have fun with it.
 
##How to use it:
 * Smile uses [google play mobile vision](https://developers.google.com/vision/)  APIs to detect
 faces, so be sure to give it a minute after installation to download the necessary APIs if not downloaded already.
 * Open the app then click on the smile FAB (Floating action button) to start the camera.
 * Smile like you've never smiled before, and hold this smile for the camera.
 * A bar on the bottom and a small avatar on the left of your face will show you how close your smile is to be captured.
 * The camera will take your picture with a lovely animation and display it to you.
 * Then you can either take other pictures or go back and enjoy your smile in your smiles mosaic or even in your gallery.
 PS: Make sure you are close enough to the device when smiling and have fun

##Snapshots:

<img src="https://raw.githubusercontent.com/m090009/Smile/master/ScreenShots/Screenshot_20160413-212923.jpg" 
     altr="Mosaic View"
     align="left" 
     height="471px" 
     width="274px"></img>
     
<img src="https://raw.githubusercontent.com/m090009/Smile/master/ScreenShots/Screenshot_20160413-213849.png" 
     altr="Camera View portrait"
     align="righ" 
     height="471px" 
     width="274px"></img>

<img src="https://raw.githubusercontent.com/m090009/Smile/master/ScreenShots/Screenshot_20160413-214002.png" 
     altr="Camera View landscape"
     align="center" 
     height="279px" 
     width="496"></img>


#Technologies
##[Mobile Vision](https://developers.google.com/vision/) 
The mobile vision APIs allow you to take advantage of a multitude of features that your handheld device can provide.
With the vision APIs your can detect faces, features, and scan barcodes, thanks to the vision APIs the app can detect 
faces and its features to determine the users' smiles.
##Design
For the design Smile uses [TwoWayView] (https://github.com/lucasr/twoway-view) and its own implementation to view images in a simple and smooth mosaic view, it also relies in [Google material icons] (https://design.google.com/icons/) for its icon needs. Smile also uses the latest Android Design support library to help deliver a better uniform experience overall.

#Here is a list of all the libraries used
  * Cardview
  * Android Design support library 23.2.1
  * Facebook Image processor [FrescoLib] (https://github.com/facebook/fresco) 0.9
  * Google's Guava 19.0
  * Google Play Services Vision Api 8.4
  * Lucasr [TwoWayView] (https://github.com/lucasr/twoway-view) 
