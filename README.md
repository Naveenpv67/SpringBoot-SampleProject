package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"strconv"
	"strings"
)

func main() {
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		fmt.Fprintf(w, "Hello, Docker!")
	})

	http.HandleFunc("/data", func(w http.ResponseWriter, r *http.Request) {
		data := map[string]interface{}{
			"message": "Welcome to the data endpoint!",
			"details": "This is some hardcoded data for demonstration.",
		}

		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusOK)

		if err := json.NewEncoder(w).Encode(data); err != nil {
			http.Error(w, "Internal Server Error", http.StatusInternalServerError)
			return
		}
	})

	http.HandleFunc("/calculate", func(w http.ResponseWriter, r *http.Request) {
		queryParams := r.URL.Query()
		operator := queryParams.Get("operator")
		num1, err1 := strconv.Atoi(queryParams.Get("num1"))
		num2, err2 := strconv.Atoi(queryParams.Get("num2"))

		if err1 != nil || err2 != nil {
			http.Error(w, "Invalid parameters", http.StatusBadRequest)
			return
		}

		result := calculate(operator, num1, num2)

		response := map[string]interface{}{
			"operator": operator,
			"num1":     num1,
			"num2":     num2,
			"result":   result,
		}

		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusOK)

		if err := json.NewEncoder(w).Encode(response); err != nil {
			http.Error(w, "Internal Server Error", http.StatusInternalServerError)
			return
		}
	})

	port := 8080
	fmt.Printf("Starting server on :%d...\n", port)
	err := http.ListenAndServe(fmt.Sprintf(":%d", port), nil)
	if err != nil {
		fmt.Printf("Error: %s\n", err)
	}
}

func calculate(operator string, num1, num2 int) int {
	switch strings.ToLower(operator) {
	case "add":
		return num1 + num2
	case "subtract":
		return num1 - num2
	case "multiply":
		return num1 * num2
	case "divide":
		if num2 == 0 {
			return 0 // Avoid division by zero
		}
		return num1 / num2
	default:
		return 0
	}
}
