<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>
      OAuth2 Password Gateway - Configuration
    </title>
    
    <link href="assets/css/bootstrap.css" rel="stylesheet">
    
    <style type="text/css">
      body {
        padding-top: 60px;
        /* 60px to make the container go all the way to the bottom of the topbar */
      }
      
    </style>
    
  </head>
  
  <body>
	<%@ include file="header.html" %>
      
      <div class="container">
		<h3>
          Setup your own OAuth2 Password Gateway
      </h3>
      <p>
	    The documentation on this page is only useful for people that want to their  own
          instance of the OAuth2 Password Gateway.  If you're not running your own instance of the OAuth2 Password Gateway,
          you can probably ignore this page.	         	     
      </p>
      
      <h4>
        Get the code
      </h4>
      <pre>
git clone git://github.com/quest/oauth2-password-gateway
</pre>
      
      
      <h4>
        Edit the web.xml file
      </h4>
      <p>
		To configure endpoints, you need to edit the servlet configuration in the web.xml file.
      </p>
      <p>
		There shouldn't be any need to change the servlet configuration for 
          <code>
            OAuth2CallbackServlet
          </code>
          . If you do change it, just remember that the redirect_uri used by the authorization code endpoint must invoke this servlet
      </p>
      <p>
		You will need to edit the servlet configuration for 
          <code>
            GoogleAuthcode2ResownerServlet
          </code>
          ,
          <code>
            FacebookAuthcode2ResownerServlet
          </code>
          , and 
          <code>
            LiveAuthcode2ResownerServlet
          </code>
          . Replace the placeholder values for client_id and client_secret with real values that you obtain from Google, Facebook and Microsoft Live. More details on how to do this below.
      </p>
      
      <h4>
        How to obtain your own values for client_id and client_secret
      </h4>
      <p>
		The following are links to instructions for setting up the OAuth2 Password Gateway as a client for Facebook, Google and Windows Live. Once setup, you will copy-n-paste the resulting client_id and client_secret values to the appropriate place in the web.xml
      </p>
      
      <ul>
		<li>
          Facebook App console: 
          <a href="https://developers.facebook.com/apps">
            https://developers.facebook.com/apps
          </a>
          
          <a class="btn btn-mini" href="/assets/img/facebook_app.png" target="blank">
            screenshot
          </a>
          </li>
          <li>
            Google App console: 
            <a href="https://code.google.com/apis/console">
              https://code.google.com/apis/console
            </a>
            
            <a class="btn btn-mini" href="/assets/img/google_app.png" target="blank">
              screenshot
            </a>
          </li>
          <li>
            Microsoft App console: 
            <a href="https://manage.dev.live.com/Applications/Index">
              https://manage.dev.live.com/Applications/Index
            </a>
            
            <a class="btn btn-mini" href="/assets/img/microsoft_app.png" target="blank">
              screenshot
            </a>
          </li>
      </ul>
      </div>
      <!-- /container -->
      
      <%@ include file="footer.html" %>     
            
      <script type="text/javascript">	
      $(document).ready(function () {
    	   $('#navConfig').addClass("active");
       });
      </script>
    </body>
</html>