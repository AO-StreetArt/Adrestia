<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="en">
  <!-- Jquery -->
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
  <!-- Popper -->
  <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
  <!-- Bootstrap -->
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
  <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootswatch/4.1.3/sandstone/bootstrap.min.css">
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
  <!-- JBox -->
  <script src="https://cdn.jsdelivr.net/gh/StephanWagner/jBox@v0.5.1/dist/jBox.all.min.js"></script>
  <link href="https://cdn.jsdelivr.net/gh/StephanWagner/jBox@v0.5.1/dist/jBox.all.min.css" rel="stylesheet">

  <!-- Custom CSS -->
  <link href="/css/aeselBrowserBaseStyle.css" rel="stylesheet">
  <head>
    <meta http-equiv="Content-Type" content="text/html" charset="utf-8"/>
    <title>Aesel</title>
  </head>
  <body>
    <div class="container-fluid pre-scrollable" style="height:100%;max-height: 100%;">
      <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <a class="navbar-brand" href="#"></a>
          <ul class="navbar-nav mr-auto">
            <li class="nav-item">
              <a class="nav-link active" href="#">Home <span class="sr-only">(current)</span></a>
            </li>
            <li class="nav-item" id="projectBrowser">
              <a class="nav-link" href="/projectBrowser">Projects</a>
            </li>
            <li class="nav-item" id="sceneBrowser">
              <a class="nav-link" href="/sceneBrowser">Scenes</a>
            </li>
            <li class="nav-item" id="assetBrowser">
              <a class="nav-link" href="/assetBrowser">Assets</a>
            </li>
            <li class="nav-item" id="docs">
              <a class="nav-link" href="https://aesel.readthedocs.io/en/latest/index.html">Documentation</a>
            </li>
          </ul>
      </nav>
      <div class="row">
    		<div class="col-md-12">
    			<h1 class="text-center">
    				Welcome to the Aesel Browser!
    			</h1>
    		</div>
    	</div>
    	<div class="row">
    		<div class="col-md-6 col-lg-6">
          <a href="https://aesel.readthedocs.io/en/latest/pages/quickstart.html">
      			<h3 class="text-center">
      				Getting Started with the Aesel Browser
      			</h3><img class="img-fluid rounded mx-auto d-block" alt="Bootstrap Image Preview" src="https://www.layoutit.com/img/sports-q-c-140-140-3.jpg" />
      			<p>
      				Getting Started with this browser for Aesel, which is a great way to explore and learn.  The Browser
              lets you explore and update the Aesel server without having to hit any API's yourself.
      			</p>
          </a>
    		</div>
    		<div class="col-md-6 col-lg-6">
          <a href="https://blendersync.readthedocs.io/en/latest/">
      			<h3 class="text-center">
      				Getting Started with BlenderSync
      			</h3><img class="img-fluid rounded mx-auto d-block" alt="Bootstrap Image Preview" src="https://www.layoutit.com/img/sports-q-c-140-140-3.jpg" />
      			<p>
      				BlenderSync integrates Aesel directly into Blender.  Explore how to create and share assets and projects,
              as well as animate collaboratively with your team.
      			</p>
          </a>
    		</div>
    	</div>
    	<div class="row">
    		<div class="col-md-6">
          <a href="https://aesel.readthedocs.io/en/latest/pages/overview.html">
      			<h3 class="text-center">
      				Aesel Workflows
      			</h3><img class="img-fluid rounded mx-auto d-block" alt="Bootstrap Image Preview" src="https://www.layoutit.com/img/sports-q-c-140-140-3.jpg" />
      			<p>
              Explore the workflows supported by the Aesel API's to build your own application using Aesel as a
              back-end service.
      			</p>
          </a>
    		</div>
    		<div class="col-md-6">
          <a href="https://aesel.readthedocs.io/en/latest/pages/DVS_API.html">
      			<h3 class="text-center">
      				Aesel API
      			</h3><img class="img-fluid rounded mx-auto d-block" alt="Bootstrap Image Preview" src="https://www.layoutit.com/img/sports-q-c-140-140-3.jpg" />
      			<p>
      				Explore the details of the Aesel API's, which can be called from any programming language.
      			</p>
          </a>
    		</div>
    	</div>
      <footer class="footer">
          <p> &copy; 2018 AO Labs</p>
      </footer>
    </div>
    <script>
    // The User ID is injected here by the server before
    // it returns the page
    var loggedInUser = "${userId}";
    </script>
  </body>
</html>
