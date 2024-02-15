package main

import (
	"encoding/json"
	"fmt"
	"net/http"

	"github.com/gorilla/mux"
)

type Greeting struct {
	Message string `json:"message"`
}

func HelloWorldHandler(w http.ResponseWriter, r *http.Request) {
	// Decode JSON payload
	var greeting Greeting
	err := json.NewDecoder(r.Body).Decode(&greeting)
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	// Perform any processing with the received data
	responseMessage := fmt.Sprintf("Hello, %s!", greeting.Message)

	// Prepare response
	response := Greeting{
		Message: responseMessage,
	}

	// Encode and send the response
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(response)
}

func main() {
	// Create a new router
	r := mux.NewRouter()

	// Define a route for the HelloWorldHandler with the POST method
	r.HandleFunc("/hello", HelloWorldHandler).Methods("POST")

	// Start the server on port 8080
	fmt.Println("Server listening on :8080")
	http.ListenAndServe(":8080", r)
}
