// If merchantName is null, this returns "" (Empty String) instead of null
addField(fieldDtos, PrmRequestField.MSG_SRC, 
    StringUtils.defaultString(StringUtils.left(prmAnalysisRequestDTO.getMerchantName(), 20))
);
