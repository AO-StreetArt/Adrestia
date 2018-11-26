<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="en">
  <!-- Jquery -->
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
  <!-- Bootstrap -->
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
  <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootswatch/4.1.3/sandstone/bootstrap.min.css">
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
  <!-- JBox -->
  <script src="https://cdn.jsdelivr.net/gh/StephanWagner/jBox@v0.5.1/dist/jBox.all.min.js"></script>
  <link href="https://cdn.jsdelivr.net/gh/StephanWagner/jBox@v0.5.1/dist/jBox.all.min.css" rel="stylesheet">

  <head>
    <meta http-equiv="Content-Type" content="text/html" charset="utf-8"/>
    <title>Aesel Asset</title>

    <style>
    html, body {
      overflow: hidden;
      width   : 100%;
      height  : 100%;
      margin  : 0;
      padding : 0;
    }
    input {
      width : 100%;
    }
    html {
      overflow-y: hidden;
    }
    </style>
  </head>
  <body>
    <div class="pre-scrollable" style="height:100%;max-height: 100%;">
    <div class="container-fluid" style="height:100%;">
      <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <a class="navbar-brand" href="#">Aesel</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarColor01" aria-controls="navbarColor01" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarColor01">
          <ul class="navbar-nav mr-auto">
            <li class="nav-item">
              <a class="nav-link" href="/portal/home">Home</a>
            </li>
            <li class="nav-item"><a class="nav-link" href="#">|</a></li>
            <li class="nav-item" id="projectBrowser">
              <a class="nav-link" href="/projectBrowser">Projects</a>
            </li>
            <li class="nav-item" id="sceneBrowser">
              <a class="nav-link" href="/sceneBrowser">Scenes</a>
            </li>
            <li class="nav-item active" id="assetBrowser">
              <a class="nav-link" href="#">Assets <span class="sr-only">(current)</span></a>
            </li>
            <li class="nav-item"><a class="nav-link" href="#">|</a></li>
            <li class="nav-item" id="docs">
              <a class="nav-link" href="https://aesel.readthedocs.io/en/latest/index.html">Documentation</a>
            </li>
          </ul>
        </div>
      </nav>
      <div class="row">
        <div class="col-md-12">
          <h1 style="text-align: center;">Asset</h1>
        </div>
      </div>
      <div class="row">
        <div class="col-md-2">
          Key:
        </div>
        <div class="col-md-10" class="tooltip" title="The unique identifier of the Scene.">
          <input id="keyinp" type="text" class="form-control-plaintext" name="Key" placeholder="Key"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-2">
          Name:
        </div>
        <div class="col-md-10" class="tooltip" title="The human-readable name of the Scene.">
          <input id="nameinp" type="text" class="form-control-plaintext" name="Name" placeholder="Name"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-2">
          Content Type:
        </div>
        <div class="col-md-10" class="tooltip" title="The latitude of the Scene for distance queries.">
          <input id="ctypeinp" type="text" class="form-control-plaintext" name="ContentType" placeholder="Content Type"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-2">
          File Type:
        </div>
        <div class="col-md-10" class="tooltip" title="The longitude of the Scene for distance queries.">
          <input id="ftypeinp" type="text" class="form-control-plaintext" name="FileType" placeholder="File Type"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-2">
          Asset Type:
        </div>
        <div class="col-md-10" class="tooltip" title="The longitude of the Scene for distance queries.">
          <input id="atypeinp" type="text" class="form-control-plaintext" name="AssetType" placeholder="Asset Type"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-2">
          Tags:
        </div>
        <div class="col-md-10" class="tooltip" title="The tags associated to the Scene.">
          <input id="tagsinp" type="text" class="form-control-plaintext" name="Tags" placeholder="Tags"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-2">
          File:
        </div>
        <div class="col-md-10" class="tooltip" title="The actual asset file to upload.">
          <input id="fileinp" name="file" type="file" class="form-control-file"></input>
        </div>
      </div>
      <div class="row">
        <div class="col-md-6" class="tooltip" title="Cancel the changes.">
          <button id="cancel" type="button" class="btn btn-primary">Cancel</button>
        </div>
        <div class="col-md-6" class="tooltip" title="Save the Asset.">
          <button id="save" type="button" class="btn btn-primary">Save</button>
        </div>
      </div>
      <footer class="footer">
          <p> &copy; 2018 AO Labs</p>
      </footer>
    </div>
    </div>
    <script>
    // The Asset Key is injected here by the server before
    // it returns the page
    var assetKey = "${assetKey}";

    function onPost(event){
      var url = "v1/asset";
      var key = document.getElementById('keyinp').value;
      if (key) {
        url = url + "/" + key;
      } else {
        var isStarted = false;
        var name = document.getElementById('nameinp').value;
        if (name) {
          url = url + "?name=" + name;
          isStarted = true;
        }
        var ctype = document.getElementById('ctypeinp').value;
        if (ctype) {
          if (!isStarted) {
            url = url + "?";
          } else {
            url = url + "&";
          }
          isStarted = true;
          url = url + "content-type=" + ctype;
        }
        var ftype = document.getElementById('ftypeinp').value;
        if (ftype) {
          if (!isStarted) {
            url = url + "?";
          } else {
            url = url + "&";
          }
          isStarted = true;
          url = url + "file-type=" + ftype;
        }
        var atype = document.getElementById('atypeinp').value;
        if (atype) {
          if (!isStarted) {
            url = url + "?";
          } else {
            url = url + "&";
          }
          isStarted = true;
          url = url + "asset-type=" + atype;
        }
        var tags = document.getElementById('tagsinp').value;
        if (tags) {
          if (!isStarted) {
            url = url + "?";
          } else {
            url = url + "&";
          }
          isStarted = true;
          url = url + "tags=" + tags;
        }
      }
      console.log(url);
      var formData = new FormData();
      formData.append("file", $("input[name=file]")[0].files[0]);
      $.ajax({
        url: url,
        method: 'POST',
        cache: false,
        contentType: false,
        data: formData,
        processData: false,
        success: function(data) {
          console.log(data);
        }
      });
    }

    // Cancel button logic
    function onButtonClick(event) {
      window.history.back();
    }

    document.getElementById('cancel').addEventListener("click", onButtonClick, false);
    document.getElementById('save').addEventListener("click", onPost, false);

    // Logic for loading the page from a pre-existing Project
    var query_return = function(data) {
      console.log("Updating Scene Data");
      console.log(data);
      document.getElementById('keyinp').value = data[0].key;
      document.getElementById('nameinp').value = data[0].name;
      document.getElementById('ctypeinp').value = data[0].contentType;
      document.getElementById('ftypeinp').value = data[0].fileType;
      document.getElementById('atypeinp').value = data[0].assetType;
      document.getElementById('tagsinp').value = data[0].tags;
    }

    window.addEventListener('DOMContentLoaded', function(){

      // Setup the cancel button callback
      var cancelButton = document.getElementById('cancel');
      cancelButton.addEventListener("click", onButtonClick, false);

      // Activate JBox Tooltips
      new jBox('Tooltip', {
        attach: '.tooltip'
      });

      console.log("Asset Key: " + assetKey)
      if (assetKey) {
        $.ajax({url: "v1/asset", data: {key: assetKey}, success: query_return});
      }
    });
    </script>
  </body>
</html>
