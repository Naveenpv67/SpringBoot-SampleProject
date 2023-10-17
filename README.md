@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Successful operation", content = {
        @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
    }),
    @ApiResponse(responseCode = "400", description = "Bad request", content = {
        @Content(mediaType = "application/json")
    }),
    @ApiResponse(responseCode = "404", description = "Debit card not found", content = {
        @Content(mediaType = "application/json")
    }),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = {
        @Content(mediaType = "application/json")
    }),
    @ApiResponse(responseCode = "422", description = "Validation Error - Blank or Null Field (Long Value)", content = {
        @Content(mediaType = "application/json")
    }),
    @ApiResponse(responseCode = "423", description = "Validation Error - Length of Long Field", content = {
        @Content(mediaType = "application/json")
    }),
    @ApiResponse(responseCode = "424", description = "Validation Error - Length of String Field", content = {
        @Content(mediaType = "application/json")
    })
})
