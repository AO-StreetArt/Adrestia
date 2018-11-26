<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Aesel Browser</title>
    <!-- Jquery -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    <!-- Bootstrap -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootswatch/4.1.3/sandstone/bootstrap.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
</head>

<body>

<div class="pre-scrollable" style="height:100%;max-height: 100%;">
  <div class="container-fluid">
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
      <a class="navbar-brand" href="#">Aesel</a>
      <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarColor01" aria-controls="navbarColor01" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarColor01">
        <ul class="navbar-nav mr-auto">
          <li class="nav-item active">
            <a class="nav-link" href="#">Home <span class="sr-only">(current)</span></a>
          </li>
          <li class="nav-item"><a class="nav-link" href="#">|</a></li>
          <li class="nav-item" id="projectBrowser">
            <a class="nav-link" href="/projectBrowser">Projects</a>
          </li>
          <li class="nav-item" id="sceneBrowser">
            <a class="nav-link" href="/sceneBrowser">Scenes</a>
          </li>
          <li class="nav-item" id="assetBrowser">
            <a class="nav-link" href="/assetBrowser">Assets</a>
          </li>
          <li class="nav-item"><a class="nav-link" href="#">|</a></li>
          <li class="nav-item" id="docs">
            <a class="nav-link" href="https://aesel.readthedocs.io/en/latest/index.html">Documentation</a>
          </li>
          <li class="nav-item"><a class="nav-link" href="#">|</a></li>
          <li class="nav-item" id="qsLogoutBtn">
            <a class="nav-link" href="#">Logout</a>
          </li>
        </ul>
      </div>
    </nav>
    <div class="jumbotron">
        <h3>Hello ${userId}!</h3>
    </div>
    <div class="row marketing">
        <div class="col-lg-6">
            <h4>Logged In!</h4>
            <p>If you're seeing this message, then you've successfully logged into Aesel.</p>
        </div>
    </div>

    <footer class="footer">
        <p> &copy; 2018 AO Labs</p>
    </footer>

</div>
</div>

<script type="text/javascript">
    $("#qsLogoutBtn").click(function(e) {
        e.preventDefault();
        $("#qsLogoutBtn").removeClass("active");
        $("#password-login").removeClass("active");
        $("#logout").addClass("active");
        // assumes we are not part of SSO so just logout of local session
        window.location = "${fn:replace(pageContext.request.requestURL, pageContext.request.requestURI, '')}/logout";
    });
</script>

</body>
</html>
