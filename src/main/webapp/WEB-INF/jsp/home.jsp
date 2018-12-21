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
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>Aesel</title>
    <link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/cookieconsent2/3.1.0/cookieconsent.min.css" />
    <script src="//cdnjs.cloudflare.com/ajax/libs/cookieconsent2/3.1.0/cookieconsent.min.js"></script>
    <script>
    window.addEventListener("load", function(){
    window.cookieconsent.initialise({
      "palette": {
        "popup": {
          "background": "#252e39"
        },
        "button": {
          "background": "#14a7d0"
        }
      }
    })});
    </script>
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
            <li class="nav-item" id="userBrowser">
              <a class="nav-link" href="/userBrowser" id="userBrowserLink">Users</a>
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
    			<h2 class="text-center">
    				Getting Started with the Aesel Browser
    			</h2>
          <a href="https://aesel.readthedocs.io/en/latest/pages/web_ui_quickstart.html">
            <img class="img-fluid rounded mx-auto d-block" alt="Aesel Web Browser Logo" src="/images/aesellearning_logo.png" />
      			<p>
      				Getting Started with this browser for Aesel, which is a great way to explore and learn the API's.
      			</p>
          </a>
    		</div>
    		<div class="col-md-6 col-lg-6">
    			<h2 class="text-center">
    				Getting Started with BlenderSync
    			</h2>
          <a href="https://blendersync.readthedocs.io/en/latest/">
            <img class="img-fluid rounded mx-auto d-block" alt="Blender Sync Logo" src="/images/aesellearning_logo.png" />
      			<p>
      				BlenderSync integrates Aesel directly into Blender.  Explore how to create and share assets and projects,
              as well as animate collaboratively with your team.
      			</p>
          </a>
    		</div>
    	</div>
      <div class="row">
    		<div class="col-md-12">
    			<h2 class="text-center">
    				Favorite Projects
    			</h2>
    		</div>
    	</div>
    	<div class="row">
    		<div class="col-md-4">
    			<h3 class="text-center" id="project1Header"></h3>
          <a href="https://aesel.readthedocs.io/en/latest/pages/overview.html">
            <img class="img-fluid rounded mx-auto d-block" alt="Aesel Project 1" src="" id="project1Thumbnail"/>
      			<p class="text-center" id="project1Description"></p>
          </a>
    		</div>
        <div class="col-md-4">
    			<h3 class="text-center" id="project2Header"></h3>
          <a href="https://aesel.readthedocs.io/en/latest/pages/overview.html">
            <img class="img-fluid rounded mx-auto d-block" alt="Aesel Workflows Animation" src="" id="project2Thumbnail" />
      			<p class="text-center" id="project2Description"></p>
          </a>
    		</div>
        <div class="col-md-4">
    			<h3 class="text-center" id="project3Header"></h3>
          <a href="https://aesel.readthedocs.io/en/latest/pages/overview.html">
            <img class="img-fluid rounded mx-auto d-block" alt="Aesel Workflows Animation" src="" id="project3Thumbnail" />
      			<p class="text-center" id="project3Description"></p>
          </a>
    		</div>
    	</div>
      <footer class="footer">
          <p> &copy; 2018 AO Labs</p>
      </footer>
    </div>
    <script>
    // Main page, added once DOM is loaded into browser
    window.addEventListener('DOMContentLoaded', function(){
      // The User ID is injected here by the server before
      // it returns the page
      var loggedInUser = "${userName}";
      console.log(loggedInUser);
      // If the user is an admin, then the server will inject 'true' here,
      // otherwise, it will inject 'false'.
      var isUserAdmin = "${isAdmin}";
      var adminLoggedIn = (isUserAdmin == 'true');
      if (!adminLoggedIn) {
        // Disable the user browser link in the navbar if the logged in
        // user does not have admin access
        document.getElementById("userBrowser").setAttribute('class', 'nav-item disabled');
        // document.getElementById("userBrowserLink").click(function() {
        //   return false;
        // });
      }
      // The favorite projects list is injected here by the server
      // before it returns the page.
      var favoriteProjectsList = "${projectsString}";
      var favProjects = favoriteProjectsList.split(",");
      if (favProjects.length > 0 && favProjects[0] != "") {
        $.ajax({url: "/v1/project/" + favProjects[0],
                type: 'GET',
                success: function(data) {
                  console.log("Project 1 Retrieved");
                  document.getElementById('project1Header').innerHTML = data.name;
                  document.getElementById('project1Description').innerHTML = data.description;
                  document.getElementById('project1Thumbnail').src = "v1/asset/" + data.thumbnail + "/thumbnail.png";
                }});
      }
      if (favProjects.length > 1) {
        $.ajax({url: "/v1/project/" + favProjects[1],
                type: 'GET',
                success: function(data) {
                  console.log("Project 2 Retrieved");
                  document.getElementById('project2Header').innerHTML = data.name;
                  document.getElementById('project2Description').innerHTML = data.description;
                  document.getElementById('project2Thumbnail').src = "v1/asset/" + data.thumbnail + "/thumbnail.png";
                }});
      }
      if (favProjects.length > 2) {
        $.ajax({url: "/v1/project/" + favProjects[2],
                type: 'GET',
                success: function(data) {
                  console.log("Project 3 Retrieved");
                  document.getElementById('project3Header').innerHTML = data.name;
                  document.getElementById('project3Description').innerHTML = data.description;
                  document.getElementById('project3Thumbnail').src = "v1/asset/" + data.thumbnail + "/thumbnail.png";
                }});
      }
    });
    </script>
  </body>
</html>
