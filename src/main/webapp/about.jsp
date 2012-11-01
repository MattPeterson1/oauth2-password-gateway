<!DOCTYPE html>
<html lang="en">
  
  <head>
    <meta charset="utf-8">
    <title>
      OAuth2 Password Gateway - About
    </title>
    <link href="assets/css/bootstrap.css"
    rel="stylesheet">
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
          About OAuth2 Password Gateway
        </h3>
        
        <p>
          Oauth2 Password Gateway is an OAuth2 authorization service that implements
          "Resource Owner Password Credentials Grant" by calling another authorization
          service as client for the more commonly implemented "Authorization Code
          Grant"
        </p>
        <h3>
          Project Goal
        </h3>
        
        <p>
          In 
          <a href="http://tools.ietf.org/html/rfc6749#section-4.3">
            section
            4.3
          </a>
          of the OAuth 2.0 spec you'll find the specification for the
          "Resource Owner Password Credentials Grant" -- including the following
          introductory paragraph:
        </p>
        
        <pre>
4.3.  Resource Owner Password Credentials Grant
The grant type is suitable for clients capable of obtaining the
resource owner's credentials (username and password, typically using
an interactive form).  It is also used to migrate existing clients
using direct authentication schemes such as HTTP Basic or Digest
authentication to OAuth by converting the stored credentials to an
access token
</pre>
        
        <p>
          Hmmm. Sounds useful for making legacy systems use OAuth2. Doesn't it?
        </p>
        <p>
          Therefore ... the best outcome of this OAuth2 Password Gateway would be
          to raise awareness of the 
          <a href="http://tools.ietf.org/html/rfc6749#section-4.3">
            OAuth 2.0 Resource Owner
            Password Credentials Grant
          </a>
          -- the hope being that it will be implemented
          by OAuth 2.0 authorization services so that a clunky gateway like this
          one won't be necessary for people that need it to make legacy systems work
          with new stuff (like OAuth 2.0).
        </p>
        <h3>
          Warning: Not for production use
        </h3>
        
        <p>
          The oauth2-passwd-gateway is just a prototype that might allow developers
          to explore what might be possible if authorization services were to provide
          real implementations of 
          <a href="http://tools.ietf.org/html/rfc6749#section-4.3">
            OAuth 2.0 Resource Owner
            Password Credentials Grant
          </a>
          .
        </p>
        <h3 id="howItWorks">
          How does it work?
        </h3>
        
        <p>
          The OAuth2 Password Gateway provides endpoints that support the Resource
          Owner Password Grant. Non-interactive clients, or non-browser based clients
          can pass username/password credentials to these endpoints.
        </p>
        <p>
          Upon receipt of an HTTP POST containing the credentials, the OAuth2 Password
          Gateway contacts the OAuth 2.0 authorization code endpoint of a real Authorization
          service (Facebook, Google, Microsoft Live). The embedded user-agent automates
          the interaction with the authorization service as if a user was actually
          present. The resulting Access Token Response is returned by the gateway
          to the original caller of the password grant endpoint.
          <p>
            On one side the OAuth2 Password Gateway is an OAuth 2.0 authorization
            server providing Resource Owner Password Grant endpoints, on the other
            side, it's a OAuth 2.0 authorization client that consumes OAuth 2.0 authorization
            code grant endpoints.
        </p>
        <img src="/assets/img/drawing.png"/>
      <h3>
        Is a gateway a good idea?
      </h3>
      
      <p>
        No. It would be much better if the real authorization service implement
        the Resource Owner Password Credential Grant.
      </p>
    </div>
    <!-- /container -->
    <%@ include file="footer.html" %>
    </body>
    <script type="text/javascript">
      $(document).ready(function () {
        $('#navAbout').addClass("active");
      });
    </script>
    
  </html>