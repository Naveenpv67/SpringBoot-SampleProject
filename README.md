Hi master, I have this custom exception class. In my entire project scope, if I want to get the error message, I want to get the error code. I'm always getting confused because I have two kinds of error codes:
One code is of integer
Another code is of string
In all places, I need to do null checks between each two things. This code is getting duplicated. I am thinking in this exception class if I will do one get code message, get error code method. In that method, only I will try to add that logic so that I can reuse the same code in all of those places instead of always doing the rechecking. Correct? But one catch point is the return type will be changing. It can be either string or even int. So, how to handle this? 
