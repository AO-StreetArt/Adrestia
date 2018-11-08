<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Aesel Browser</title>
    <link rel="stylesheet" type="text/css" href="/css/bootstrap.css">
    <link rel="stylesheet" type="text/css" href="/css/jumbotron-narrow.css">
    <link rel="stylesheet" type="text/css" href="/css/home.css">
    <link rel="stylesheet" type="text/css" href="/css/jquery.growl.css"/>
    <script src="http://code.jquery.com/jquery.js"></script>
    <script src="/js/jquery.growl.js" type="text/javascript"></script>
</head>

<body>

<div class="container">
    <div class="header clearfix">
        <nav>
            <ul class="nav nav-pills pull-right">
                <li class="active" id="home"><a href="#">Home</a></li>
                <li id="sceneBrowser"><a href="/sceneBrowser">Scene Browser</a></li>
                <li id="assetBrowser"><a href="/assetBrowser">Asset Browser</a></li>
                <li id="qsLogoutBtn"><a href="#">Logout</a></li>
            </ul>
        </nav>
    </div>
    <div class="jumbotron">
        <h3>Hello ${userId}!</h3>
    </div>
    <div class="row marketing">
        <div class="col-lg-6">
            <h4>Logged In!</h4>
            <p>If you're seeing this message, then you've successfully logged into Aesel.</p>
        </div>

        <div class="col-lg-6">
            <h4>Use this Info</h4>
            <p>You can copy this information into your Aesel client.</p>
        </div>
    </div>

    <footer class="footer">
        <p> &copy; 2018 AO Labs</p>
    </footer>

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
