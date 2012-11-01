[OAuth2 Password Gateway](https://oauth2-password-gateway.herokuapp.com/)
=========================

Oauth2 Password Gateway is an OAuth2 authorization service that provides a "Resource Owner Password Credentials Grant" by calling another authorization service as client using the more commonly implemented "Authorization Code Grant"

Mission
-------
In [section 4.3](http://tools.ietf.org/html/rfc6749#section-4.3) of the OAuth 2.0 spec you'll find the specification for the "Resource Owner Password Credentials Grant" -- including the following introductory paragraph:

> The grant type is suitable for clients capable of obtaining the
> resource owner's credentials (username and password, typically using
> an interactive form).  It is also used to migrate existing clients
> using direct authentication schemes such as HTTP Basic or Digest
> authentication to OAuth by converting the stored credentials to an
> access token

Hmmm.  Sounds useful for makeing legacy systems use OAuth2...  doesn't it?  Indeed.

The best outcome of this OAuth2 Password Gateway would be to raise awareness of the [OAuth 2.0 Resource Owner Password Credentials Grant](http://tools.ietf.org/html/rfc6749#section-4.3) -- the hope being that it will be implemented by OAuth 2.0 authorization services so that a clunky gateway like this one won't be necessary for people, like me, that need it to make legacy systems work with new stuff (like OAuth 2.0).

*Warning: The oauth2-passwd-gateway is not intended to be deployed as production solution!*

Skip to the demo
----------------
If you don't enjoy reading IETF RFC specs, the next best way to learn about the Resource Owner Password Credentials Grant is to use a hosted instance of the OAuth2 Password Gateway with your favorite Authorization Servers (Google, Facebook, and Windows Live). 

[Click here to try it now](https://oauth2-password-gateway.herokuapp.com/)


Setup your own OAuth2 Password Gateway
--------------------------------------

1) Clone the repo: `git clone git://github.com/quest/oauth2-passwd-gateway`

2) Edit the /src/main/webapp/WEB-INF/web.xml file to add your own `client_id`, `client_secret`, and `redirect_uri` values to the servlet init-params .

3) Build the build a war file:  mvn package

4) Deploy the war to your server.

If you're just messing around, you might find that it's really easy to deploy oauth2-passwd-gateway to [Heroku](https://devcenter.heroku.com/articles/java).

