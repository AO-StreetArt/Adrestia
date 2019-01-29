// Function to display an alert for a short period of time
function displayAlert(alertDocId) {
  $("#" + alertDocId).show();
  setTimeout(function() { $("#" + alertDocId).hide(); }, 5000);
};

// Function for showing an edit button on a text object
// during input events (ie. scrolled over)
function showEditButton(eltId) {
  $("#" + eltId).show();
};

function showHtmlElements(eltIds) {
  for (i = 0; i < eltIds.length; i++) {
    showEditButton(eltIds[i]);
  }
};

function showEditButtons(eltIds) {
  showHtmlElements(eltIds);
};

// Function for hiding an edit button on a text object
// during input events (ie. cursor leaves div)
function hideEditButton(eltId) {
  $("#" + eltId).hide();
};

function hideHtmlElements(eltIds) {
  for (i = 0; i < eltIds.length; i++) {
    hideEditButton(eltIds[i]);
  }
};

function hideEditButtons(eltIds) {
  hideHtmlElements(eltIds);
};

// Get the file type of a file
function getFileExtension(filename) {
  var parts = filename.split('.');
  return parts[parts.length - 1];
}

// Disable the user browser link in the navbar if the
// logged in user does not have admin access
function setUsersLink(adminLoggedIn, browserLinkEltId, loggedInKey) {
  if (!adminLoggedIn) {
    document.getElementById(browserLinkEltId).href = "/editUser?key=" + loggedInKey;
    document.getElementById(browserLinkEltId).innerHTML = "My Account";
  }
}

// Setup a single callback method for all buttons
function registerButtonCallback(callbackMethod) {
  var buttons = document.getElementsByTagName("button");
  for (let i = 0; i < buttons.length; i++) {
    buttons[i].addEventListener("click", callbackMethod, false);
  };
}
