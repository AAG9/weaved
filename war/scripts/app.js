
'use strict';

var weave = window.weave || {};


(function () {
  var app = document.getElementById('app');

  app.baseUrl = '/';
  /**
   * Listen for event when template gets attached to the page and bind event
   * handlers
   */
 /* app.addEventListener('dom-change', function () {
    var signIn = document.querySelector('google-signin');
    signIn.addEventListener('google-signin-success', app.signedIn.bind(this));
    signIn.addEventListener('google-signed-out', app.signedOut.bind(this));
    signIn.addEventListener('google-signin-offline-success', app.offlineSuccess.bind(this));
  });*/
  
  

  /**
   * Handle sign in and start api discovery process
   */
  app.signedIn = function (event) {
	  console.log("event");
	  console.log(event);
    // window.location.href="http://localhost:8080/home.html";
    /*gapi.client.weave.devices.list().then(function (resp) {
      var userEmail = gapi.auth2.getAuthInstance().currentUser.get().wc.hg;
      //console.log(userEmail);
      document.getElementById('afterSignIn').innerHTML = userEmail;

      app.set('devices', resp.result.devices);

      var name = gapi.auth2.getAuthInstance().currentUser.get().wc.wc;
      document.getElementById('name').innerHTML = name;

      var totalResults = resp.result.totalResults
      document.getElementById('totalDevices').innerHTML = " - "+totalResults;
      // alert(resp.result.totalResults);
      // alert(resp.result.devices[1].kind);
      window.onload = function() {
        alert("in")
        var deviceInfo = JSON.stringify(resp.result.devices[0]);
        console.log(deviceInfo);
        document.getElementById('loadDeviceInfo').innerHTML = deviceInfo;
      } 
            
    });*/
    
  };
  
  /**
   * Handle offline authorization and call Servlet 
   */
  app.offlineSuccess = function (authResult) {
	  console.log("hi");
	  console.log(authResult);
	  if (authResult.detail.code) {
		  alert("hi");
		  
		// Send the code to the server
	    $.ajax({
	      type: 'POST',
	      url: 'http://localhost:8080/AmeyaWeave/myServ?',
	      contentType: 'application/octet-stream; charset=utf-8',
	      success: function(result) {
	        // Handle or verify the server response.
	        alert("Success");
	      },
	      processData: false,
	      data: authResult.detail.code
	    });
	  } else {
	    // There was an error.
		  alert("error");
	  }
  }
  

  window.addEventListener('paper-header-transform', function(e) {
    var appName = Polymer.dom(document).querySelector('#mainToolbar .app-name');
    var middleContainer = Polymer.dom(document).querySelector('#mainToolbar .middle-container');
    var bottomContainer = Polymer.dom(document).querySelector('#mainToolbar .bottom-container');
    var detail = e.detail;
    var heightDiff = detail.height - detail.condensedHeight;
    var yRatio = Math.min(1, detail.y / heightDiff);
    // appName max size when condensed. The smaller the number the smaller the condensed size.
    var maxMiddleScale = 0.50;
    var auxHeight = heightDiff - detail.y;
    var auxScale = heightDiff / (1 - maxMiddleScale);
    var scaleMiddle = Math.max(maxMiddleScale, auxHeight / auxScale + maxMiddleScale);
    var scaleBottom = 1 - yRatio;

    // Move/translate middleContainer
    Polymer.Base.transform('translate3d(0,' + yRatio * 100 + '%,0)', middleContainer);

    // Scale bottomContainer and bottom sub title to nothing and back
    Polymer.Base.transform('scale(' + scaleBottom + ') translateZ(0)', bottomContainer);

    // Scale middleContainer appName
    Polymer.Base.transform('scale(' + scaleMiddle + ') translateZ(0)', appName);
  });

  // Scroll page to top and expand header
  app.scrollPageToTop = function() {
    app.$.headerPanelMain.scrollToTop(true);
  };

  app.closeDrawer = function() {
    app.$.paperDrawerPanel.closeDrawer();
  };

  app.authorizeNewDevices = function() {
    var ajax = document.getElementById('ajax');
    var token = gapi.auth2.getAuthInstance().currentUser.get().getAuthResponse();
    var authHeader = {"Authorization": "Bearer " + token.access_token};
    ajax.headers = authHeader;
    ajax.generateRequest();
  };
  
  app.runDeviceCommands = function(){
	  alert("In run device commands");
	  var editor = ace.edit("editor");
	  var myVar = editor.getSession().getValue();
	  alert(myVar);

	  // Send the code to the server
	    $.ajax({
	      type: 'GET',
	      url: '/AmeyaWeave/runCommands?jsonScript='+ myVar.toString(),
	      success: function(jsonScript) {
	        // Handle or verify the server response.
	        alert("Success");
	      },
	      processData: false
	    });
  }
  
  app.googleBackendSignin = function(){
	  console.log("In backend google signin");
	  location.href = 'https://accounts.google.com/o/oauth2/auth?client_id=309435708548-fsvfu060n29531ufr5qqqgf7t2jhhvan.apps.googleusercontent.com&redirect_uri=http://localhost:8888/ExServlet&response_type=code&scope=https://www.googleapis.com/auth/weave.app';
  }

  app.ajaxHandler = function() {
    var ajax = document.getElementById('ajax');
    var weaveToken = ajax.lastResponse.token;
    var authLocation = 'https://weave.google.com/manager/share?role=user&token=' +
     weaveToken + '&redirect_url=' + encodeURIComponent(location.origin);
    var windowFeatures = 'menubar=no,location=no,height=600,width=450,chrome=yes,centerscreen=yes,alwaysRaised=yes';
    app.authWindow = window.open(authLocation, "Weave Device Authorization", windowFeatures);
  };

  /**
   * Handle sign out and clear devices
   */
  app.signedOut = function () {
    app.set('devices', null);
    document.getElementById('afterSignIn').innerHTML = null;
  };

  /**
   * Load discovery json and get a list of devices
   */
  app.loadDiscovery = function () {
    gapi.client.load(weaveDiscovery, 'v1')
        .then(function () {
          console.log('gapi loaded');
        }, function () {
          console.log('gapi error');
        })
  };

  /**
   * Helper method to recurse through the JSON object and pull out commandDef strings.
   *
   * @param currElement {Object} current object
   * @param key {String} key of current object
   * @param path {String} string containing key path to object
   * @returns {Array} of objects defining the commandDef and parameters
   */
  app.getCommandDefs = function (currElement) {
    if (currElement.commandDefs) {
      console.log('parsing command defs');
      return app.recurseCommandDefs(currElement.commandDefs);
    } else if(currElement.traits) {
      console.log('parsing components');
      var commands = [];
      for (var prop in currElement.traits) {
        if (currElement.traits.hasOwnProperty(prop)) {
          commands = commands.concat(app.parseTraits(currElement.traits[prop], prop));
        }
      }
      return commands;
    }
  };

  app.recurseCommandDefs = function (currElement, key, path) {
    var commands = [];
    if (!path) {
      path = [];
    }
    var currPath = path.slice();
    if (key) {
      currPath.push(key);
    }
    if (currElement.kind && currElement.kind === "weave#commandDef") {
      var parameters = [];
      for (var param in currElement.parameters) {
        currElement.parameters[param].parameter = param;
        parameters.push(currElement.parameters[param]);
      }
      commands.push({
        'command': currPath.join('.'),
        'parameters': parameters
      });
    } else {
      for (var currKey in currElement) {
        Array.prototype.splice.apply(
            commands, [commands.length, 0].concat(
                app.recurseCommandDefs(currElement[currKey], currKey, currPath)));
      }
    }
    return commands;
  };

  app.parseTraits = function(currElement, key, path) {
    var outputCommands = [];

    if (!path) {
      path = [];
    }

    var currPath = path.slice();

    if (key) {
      currPath.push(key);
    }

    var commands;
    if (currElement.commands) {
      commands = currElement.commands;
    }

    for (var command in commands) {
      if (commands.hasOwnProperty(command)) {
        outputCommands.push({
          'command': currPath.join('.') + '.' + command,
          'parameters': commands[command].parameters
        })
      }
    }

    return outputCommands;
  };
  /**
   * Checks if the current commandDef is _ledflasher._set
   *
   * @param param {String} commandDef string
   * @returns {boolean} if the commandDef string matches _ledflasher._set
   */
  app.isSet = function (param) {
    return (param.command === "_ledflasher._set" || param.command === "_ledflasher.set");
  };

  /**
   * Checks if the current led on the device is on
   *
   * @param device {Object} device to check against
   * @param id {Number} id of led
   * @returns {Boolean} true if the device led is on
   */
  app.isActive = function (device, id) {
    return device.state._ledflasher._leds[id - 1];
  };

  /**
   * Returns true if the device is offline
   *
   * @param device {Object} device to check against
   * @returns {boolean} true if device connectionStatus is offline
   */
  app.isOffline = function (device) {
    return device.connectionStatus === "offline"
  };

  /**
   * Converts the range of led ids into an array of led objects
   *
   * @param param {Object} commandDef parameters
   * @returns {Array} of objects in the following format
   * {
 *   'command': [command],
 *   'id': [id of led]
 * }
   */
  app.rangeToArray = function (param, device) {
    var leds = [];
    if(device.components) {
      for (i = param.parameters.led.minimum; i <= param.parameters.led.maximum; i++) {
        leds.push({
          'command': param.command,
          'id': i,
          'state': device.components.ledflasher.state._ledflasher.leds[i - 1]
        });
        
      }
    } else if (device.state) {
      for (var i = param.parameters[0].minimum; i <= param.parameters[0].maximum; i++) {
        leds.push({
          'command': param.command,
          'id': i,
          'state': device.state._ledflasher._leds[i - 1]
        });
      }
    }
    return leds;
  };

  /**
   * Sends a request to toggle the led triggering the event
   *
   * @param event {Event}
   */
  app.toggleLed = function (event) {
    var commandParameter = {
      'deviceId': event.target.device,
      'name': event.target.command,
      'parameters': {
        '_led': event.target._led,
        '_on': event.target.active
      }
    };
    gapi.client.weave.commands.insert(commandParameter).then(function (resp) {
      console.log('success' + JSON.stringify(resp));
    }, function (resp) {
      console.log('failure' + JSON.stringify(resp));
    });
  };

  window.addEventListener("message", function(event) {
    console.log(event);
  }, false);
  weave.app = app;
})();


function gapiLoaded() {
  weave.app.loadDiscovery();
}

function onSignIn(googleUser) {
	  var profile = googleUser.getBasicProfile();
	  console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
	  console.log('Name: ' + profile.getName());
	  console.log('Image URL: ' + profile.getImageUrl());
	  console.log('Email: ' + profile.getEmail());
	  
	  googleUser.grantOfflineAccess({scope:'https://www.googleapis.com/auth/weave.app'}).then(function(resp){
		  var auth_code = resp.code;
		  console.log('Authcode:' +auth_code);
		  alert(auth_code);
		  $.ajax({
		      type: 'POST',
		      url: 'http://localhost:8888/weaved',
		      contentType: 'application/octet-stream; charset=utf-8',
		      success: function(id_token) {
		        // Handle or verify the server response.
		        alert("Success");
		      },
		      processData: false,
		      data: auth_code
		    });
	  });
	  var id_token = googleUser.getAuthResponse().id_token;
	  var access_token=googleUser.getAuthResponse().access_token;
	  alert(access_token);
	// Send the code to the server
	    
}

function signOut() {
    var auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function () {
      console.log('User signed out.');
    });
}


