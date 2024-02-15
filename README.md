package main

import (
	"encoding/json"
	"encoding/xml"
	"fmt"
	"io/ioutil"
	"net/http"
)

func main() {
	http.HandleFunc("/step", StepHandler)
	fmt.Println("Server listening on :8080")
	http.ListenAndServe(":8080", nil)
}

func StepHandler(w http.ResponseWriter, r *http.Request) {
	// Read the request body
	body, err := ioutil.ReadAll(r.Body)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	// Decode JSON request body to map
	var jsonBody map[string]interface{}
	err = json.Unmarshal(body, &jsonBody)
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	// Convert JSON data to XML
	xmlData, err := jsonToXML(jsonBody)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	// Call sample function (replace this with your logic)
	result := sampleFunction(xmlData)

	// Convert XML result back to JSON
	jsonResult, err := xmlToJSON(result)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	// Send JSON response
	w.Header().Set("Content-Type", "application/json")
	w.Write(jsonResult)
}

func jsonToXML(jsonData map[string]interface{}) (string, error) {
	xmlData, err := xml.MarshalIndent(jsonData, "", "  ")
	if err != nil {
		return "", err
	}

	return string(xmlData), nil
}

func xmlToJSON(xmlData string) ([]byte, error) {
	var data map[string]interface{}
	err := xml.Unmarshal([]byte(xmlData), &data)
	if err != nil {
		return nil, err
	}

	jsonData, err := json.Marshal(data)
	if err != nil {
		return nil, err
	}

	return jsonData, nil
}

func sampleFunction(xmlData string) string {
	// Replace this with your actual logic
	// This is just a sample function
	return fmt.Sprintf("<message>%s processed</message>", xmlData)
}
