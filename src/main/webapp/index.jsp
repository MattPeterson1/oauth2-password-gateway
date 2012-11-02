<!DOCTYPE html>
<html lang="en">
  
  <head>
    <meta charset="utf-8">
    <title>
      OAuth2 Password Gateway
    </title>
    <link href="assets/css/bootstrap.css" rel="stylesheet">
    <style type="text/css">
      
      body {
        padding-top: 60px;
        /* 60px to make the container go all the way to the bottom of the topbar */
      }      
      
      .custom {
        width: 480px;
      }
    </style>
  </head>
  
  <body>
<%@ include file="header.html" %>
      <div class="container">
        <h3>
          End Points
        </h3>
        
        <p>
          This OAuth2 Password Gateway provides 
          <a href="http://tools.ietf.org/html/rfc6749#section-4.3">
            OAuth 2.0
            Resource Owner Password Credential Grant
          </a>
          for the following:
        </p>
        <table
        id="endpointsTable" class="table">
          <thead>
            <tr>
              <th>
                Gateway To
              </th>
              <th>
                Endpoint URL
              </th>
              <th>
                Configured
              </th>
            </tr>
          </thead>
          <tbody>
<% 
String base = request.getRequestURL().toString();
base = base.substring(0,base.lastIndexOf('/'));
ServletContext context = getServletContext();
for(ServletRegistration r : context.getServletRegistrations().values())
{
    Class c = Class.forName(r.getClassName());
    if(com.quest.oauth2.passwdgateway.web.AbstractAuthcode2ResownerServlet.class.isAssignableFrom(c))
    {
		String className = r.getClassName();
		String name = r.getInitParameter("display_name");
		String clientId = r.getInitParameter("client_id");
		String configStyle = "success";
		String configHtml = "<div style=\"float:left;width:50%;\"><i class=\"icon-ok\"></i></div><div style=\"float:right;width:50%;\"><button class=\"btn btn-mini\">Try It!</button></div>";
		if(clientId == null || clientId.isEmpty() || clientId.equalsIgnoreCase("PUT YOUR CLIENT ID HERE") || r.getMappings().isEmpty() )
		{
		    configStyle = "error";
		    configHtml = "<div style=\"float:left;width:50%;\"><i class=\"icon-remove\"></i></div><div style=\"float:right;width:50%;\"><a href=\"config.jsp\">help me</a></div>";
		}		
	    out.println("<tr class=\""+configStyle +"\"/>");
	    out.println("<td>"+name+"</td>");
	    out.print("<td class=\"endpoint\">");
	    out.println( base + r.getMappings().iterator().next() );
		
	    out.println("</td>");
	    out.println("<td>"+configHtml+"</td>");
	    out.println("<td class=\"className\" hidden=\"hidden\">"+className+"</td>");
	    
		out.println("</tr>");		
    }				    
}		
%>
            </tbody>
          </table>
        
        <div id="buildRequest" hidden="hidden" class="well">
          <div class="row">
            <div class="span11">
              <h4>
                Step 1: Build a Password Grant request
              </h4>
              <hr>
            </div>
          </div>
          <div class="row">
            <div class="span4" style="border-right: 1px solid white;">
              <form id="inputForm" action="">
                <div class="control-group">
                  <label class="control-label" id="inputUsernameLabel" for="inputUsername">
                    Username:
                  </label>
                  <div class="controls">
                    <input type="text" id="inputUsername" placeholder="enter email">
                  </div>
                </div>
                <div class="control-group">
                  <label class="control-label" for="inputPassword">
                    Password:
                  </label>
                  <div class="controls">
                    <input type="password" id="inputPassword" placeholder="enter password">
                  </div>
                </div>
                <div class="control-group">
                  <label class="control-label" for="inputScope">
                    Scope:   
                    <small>
                      <a id='about_scope' data-placement='right' title="Access Token Scope" href="#">
                        (what is a scope?)
                      </a>
                    </small>
                  </label>
                  <div id="popover_content" style="display: none">
                    <div>
                      Allows the client to specify the 
                      <a href="http://tools.ietf.org/html/draft-ietf-oauth-v2-31#section-3.3">
                        permissions
                      </a>
                      of the access request.  
                      The scope parameter is expressed as a list of space-delimited strings:		
                      <ul>
                        <li>
                          <a href="http://developers.facebook.com/docs/reference/login/user-friend-permissions/">
                            Facebook Permissions
                          </a>
                        </li>
                        <li>
                          <a href="https://developers.google.com/accounts/docs/OAuth2Login#scopeparameter">
                            Google Permissions
                          </a>
                        </li>
                        <li>
                          <a href="http://msdn.microsoft.com/en-us/library/live/hh243646.aspx">
                            Microsoft Live Permissions
                          </a>
                        </li>
                        
                      </ul>
                    </div>
                    
                  </div>
                  <div class="controls">
                    <input type="text" id="inputScope" placeholder="enter scope">
                  </div>
                </div>
              </form>
            </div>
            <div class="span6">
              <form id="postForm" action="">
                <div class="control-group">
                  <label class="control-label" id="postTo" for="endpoint">
                    Post to endpoint:
                  </label>
                  <div class="controls">
                    <input class="span7" type="text" id="endpoint" placeholder="">
                  </div>
                </div>
                <div class="control-group">
                  <label class="control-label" for="postBody">
                    This is the post message (password obscured) that will be posted:
                  </label>
                  <div
                  class="controls">
                    <textarea class="span7" style="resize: none;" id="postMessage" rows="5"
                    cols="">
                    </textarea>
                  </div>
                  </div>
                </form>
              </div>
            </div>
            <div class="row">
              <div class="span11">
                <hr>
              	<h4>
                  Step 2: Post the request
              </h4>
              
              <div class="control-group">
                
                <div class="controls">
                  <button class="btn custom" id="curlButton">
                    I want to use the CURL cmdline Utility
                    </button>
                </div>
                <div class="controls">
                  <button class="btn custom" id="wgetButton">
                    I want to use the wget cmdline Utility
                    </button>
                </div>
                <div class="controls">
                  <button class="btn btn-primary custom" id="submitButton">
                    Post using Javascript from this browser
                    </button>
                </div>
              </div>
            </div>
            </div>
        </div>
        
        <div id="cutNPaste" hidden="hidden">
          <div class="row">
            <div class="span11">
              <hr>
              <h4>
                Step 3: Cut and paste the following command line:
              </h4>
              
            </div>
          </div>
          <div class="row">
            <div class="span11">
              
              <pre id="commandLine">
</pre>
              </div>
              
             </div>
             
             <div class="row">
               <div class="span11">
                 You should get a successful response that looks like the following: 
                 <pre class="success">
HTTP/1.1 200 OK
Content-Type: application/json
{
"expires": "5184000",
"access_token": "AAAFDhKxwiugBAG1dKvqWYZASAkp27alfGealCLZBFb2v5TzDrprMiJik4CkB0Uqj5yX4qJlZAnUSZC2Im8ZA7MZBLswwglfgpdXMOLspq25wZDZD"
}
</pre>
              </div>
              
             </div>
             <div class="row">
               <div class="span11">
                 ... Or you might get a error response that looks like the following: 
                 <pre class="error">
HTTP/1.1 400 Bad Request
Content-Type: application/json
{
"error": "invalid_request",
"error_description": "username is required"
}
</pre>
              </div>
              
             </div>
             
        </div>
        
        
        <div id="checkResponse" hidden="hidden">
          <div class="row">
            <div class="span11">
              <hr>
              <h4>
                Step 3: Check out the response
              </h4>
              
            </div>
          </div>
          <div class="row">
            <div class="span11">
              
              <pre id="responseMessage">
</pre>
            </div>
          </div>
        </div>
        
        <div id="useAccessCode" hidden="hidden" class="row">
          <div class="span11">
            <hr>
            <div class="span5">
              <h4>
                Step 4: Use the access_code
              </h4>
              <p>
                The 
                <code>
                  access_token
                </code>
                in the response above is what you'll need to call APIs.
                The following are popular APIs that you might be interested in calling.
                (Note: An 
                <code>
                  access_token
                </code>
                used with an API must be issued by the authorization
                service that correlates with the API. For example, you can not use a Facebook
                access_token to call a Google API or visa versa):
              </p>
              <ul>
                <li>
                  <a href="http://developers.facebook.com/docs/reference/login/">
                    Facebook Graph API
                  </a>
                  
                </li>
                <li>
                  <a href="https://developers.google.com/">
                    Google APIs
                  </a>
                  
                </li>
                <li>
                  <a href="http://msdn.microsoft.com/library/live/">
                    Microsoft Live APIs
                  </a>
                  
                </li>
              </ul>
            </div>
            <div id="lazyPeople" class="span5">
              <h4>
                For Lazy People:
              </h4>
              
              <p>
                So... Like most people you're too lazy to read documentation. For immediate
                gratification you can use the following link to get more information about
                the user that the access_token was issued for:
              </p>
              <dl id="apiCall">
                
                <dt>
                  Nothing
                </dt>
                <dd>
                  <a href="">
                    No Link
                  </a>
                </dd>
              </dl>
            </div>
          </div>
        </div>
        <div id="tryAgain" hidden="hidden" class="row">
          <div class="span11">
            
            <a id="tryAgainButton" class="btn">
              Try Again
            </a>
          </div>
        </div>
   </div>
   <!-- /container -->
   
<%@ include file="footer.html" %>  
   
   <script type="text/javascript">
   var opg = {};

   (function ($) {
     $.fn.scrollTo = function () {

       $('html, body').animate({
         scrollTop: this.position().top
       }, 'slow');
     };
   })(jQuery);

   function parseUrl(url) {
     var a = document.createElement('a');
     a.href = url;
     return a;
   }

   function hideEverything() {
     $('#cutNPaste').hide();
     $('#checkResponse').hide();
     $('#useAccessCode').hide();
     $('#tryAgain').hide();
   }

   function getPostMessage(hidePassword) {
     var postBody = "grant_type=password";
     var username = $('#inputUsername').val();
     var password = $('#inputPassword').val();
     var scope = $('#inputScope').val();
     if (username != "") postBody += "&username=" + username;
     if (hidePassword == true && password != "") postBody += "&password=" + password.replace(/./g, "X");
     if (hidePassword == false && password != "") postBody += "&password=" + password;
     if (scope != "") postBody += "&scope=" + scope;
     return postBody;
   }

   function updatePostMessage() {
     var url = parseUrl($('#endpoint').val());
     var newPostBody = "POST " + url.pathname + " HTTP/1.1\n" + "Host: " + url.host + "\n" + "Content-Type: application/x-www-form-urlencoded\n" + "\n" + getPostMessage(true);
     $('#postMessage').val(newPostBody);
   }

   function setResponse(error, errorText, content) {
     $('#responseMessage').text("HTTP/1.1 " + error + " " + errorText + "\n" + "Content-Type: application/json\n" + content);
     if (error == 200) {
       $('#responseMessage').css('background-color', '#DFF0D8');
       $('#useAccessCode').show();
     } else {
       $('#responseMessage').css('background-color', '#F2DEDE');
       $('#tryAgain').show();
     }

     $('#tryAgainButton').click(function () {
       hideEverything();
       $('#postRequest').scrollTo();

     });

     $('#checkResponse').show();
     $('#checkResponse').scrollTo();

   }

   function setApiCallUrl(provider, url, access_token) {
     var apiProvider = $('#apiCall').find('dt');
     var apiLink = $('#apiCall').find('a');
     var linkText = url + access_token.substring(0, 8) + '...';
     var linkHref = url + access_token;
     apiProvider.html(provider);
     apiLink.attr('href', linkHref);
     apiLink.text(linkText);
   }

   function setApiCall(access_token) {
     if (opg.className == 'com.quest.oauth2.passwdgateway.web.FacebookAuthcode2ResownerServlet') {
       setApiCallUrl('Facebook Graph API', 'https://graph.facebook.com/me?access_token=', access_token);
     }
     if (opg.className == 'com.quest.oauth2.passwdgateway.web.GoogleAuthcode2ResownerServlet') {
       setApiCallUrl('Google Userinfo API', 'https://www.googleapis.com/oauth2/v1/userinfo?access_token=', access_token);
     }
     if (opg.className == 'com.quest.oauth2.passwdgateway.web.LiveAuthcode2ResownerServlet') {
       setApiCallUrl('Microsoft Live API', 'https://apis.live.net/v5.0/me?&access_token=', access_token);
     }
   }

   function setScope() {
     if (opg.className == 'com.quest.oauth2.passwdgateway.web.FacebookAuthcode2ResownerServlet') {
       $('#inputUsernameLabel').text('Facebook Email:');
       $('#inputScope').val('');
       $('#inputScope').attr('placeholder', '');

     }
     if (opg.className == 'com.quest.oauth2.passwdgateway.web.GoogleAuthcode2ResownerServlet') {

       $('#inputUsernameLabel').text('Google Email:');
       $('#inputScope').val('https://www.googleapis.com/auth/userinfo.profile');
     }
     if (opg.className == 'com.quest.oauth2.passwdgateway.web.LiveAuthcode2ResownerServlet') {
       $('#inputUsernameLabel').text('Windows Live Email:');
       $('#inputScope').val('wl.basic');
     }
   }

   $(document).ready(function () {

     $('#navHome').addClass("active");

     $('#endpointsTable').find('button').click(function () {
       opg.endpoint = $(this).parents('tr').find('td.endpoint').html();
       opg.className = $(this).parents('tr').find('td.className').html();
       $('#endpoint').val(opg.endpoint);
       $('#buildRequest').show();
       $('#postRequest').show();
       setScope();
       updatePostMessage();
       hideEverything();
       $('#buildRequest').scrollTo();

     });

     $('#inputForm').find('input').keyup(function () {
       updatePostMessage();
     });

     $('#curlButton').click(function () {
       hideEverything();

       $('#commandLine').text("'curl --data '" + getPostMessage(false) + "' " + opg.endpoint);

       $('#cutNPaste').show();
       $('#useAccessCode').show();
       $('#cutNPaste').scrollTo();
     });

     $('#wgetButton').click(function () {
       hideEverything();

       $('#commandLine').text("wget -qO- --post-data '" + getPostMessage(false) + "' " + opg.endpoint);

       $('#cutNPaste').show();
       $('#useAccessCode').show();
       $('#cutNPaste').scrollTo();
     });


     $('#submitButton').click(function () {
       hideEverything();
       $.ajax({
         type: "post",
         url: opg.endpoint,
         data: getPostMessage(false),
         success: function (data, text) {
           setResponse(200, "OK", JSON.stringify(data, undefined, 4));
           setApiCall(data.access_token);
         },
         error: function (request, status, error) {
           try {
             var errorObj = JSON.parse(request.responseText);
             setResponse(request.status, request.statusText, JSON.stringify(errorObj, undefined, 4));
           } catch (err) {
             setResponse(request.status, request.statusText, request.responseText);
           }
         },
         beforeSend: function () {
           $('body').css('cursor', 'wait');
         },
         complete: function () {
           $('body').css('cursor', 'auto');
         }
       });

       return false;
     });

     $('#about_scope').click(function (e) {
       e.preventDefault();
     });

     $('#about_scope').popover({
       trigger: 'focus',
       html: true,
       content: function () {

         return $('#popover_content').html();
       }
     });

   });
   </script>
   </body>
</html>
