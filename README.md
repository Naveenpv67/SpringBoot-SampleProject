// filterDebitCards applies a filter based on the channelId and additional conditions
func filterDebitCards(cards []models.DebitCardDetails, channelId string) []models.DebitCardDetails {
    var filteredList []models.DebitCardDetails

    for _, card := range cards {
        if card.PrimaryAccount == "50100114641926" && channelId == "BB55" {
            // Apply additional conditions or logic if needed
            filteredList = append(filteredList, card)
        }
    }

    return filteredList
}

// GetDCDetails is your controller function
func (c Controller) GetDCDetails(rw http.ResponseWriter, r *http.Request) {
    ctx := r.Context()

    var (
        req      models.DCDetailsInp
        err      error
        response models.DCDetailsResp
    )

    channelId := os.Getenv("CHANNEL_ID")

    // Assume you have obtained the list of cards in some way and stored it in the 'cards' variable

    // Call the filterDebitCards function
    filteredList := filterDebitCards(cards, channelId)

    // Continue with the remaining code using the filteredList
    // ...
}
