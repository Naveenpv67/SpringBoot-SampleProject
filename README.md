
Hi,

I am a software engineer. I am implementing robust exception handling. I have two services:
SDK backend service
Issuer service

Here, the issuer service is the main core service, and the SDK backend service is the simple service which will only act as an API gateway. That is, I have a web client call in the SDK service which will simply redirect all the calls to the issuer service and collect the response. Not collect, like send back the same response. Here, I have handled exceptions in the issuer service very robustly and efficiently, but in the SDK backend service, in the exception handling, like in the web client call on error, I want to handle it more efficiently. If it is a type of custom exception, then don't do anything, don't alter any other message. So as it is, that is what I want to change. Would you please help me on this? 
