public class MerchantInfo {
    private String merchantId;
    private String terminalId;
    private String description;
    private String riskProfileId;
    private String paymentInstrument;
    private String brandName;
    private String currency;
    private String terminalAliasName;
    private String tranportalId;
    private String tranportalPassword;
    private String externalConnectionId;
    private String cardAcceptorId;
    private String cardAcceptorTerminalId;
    private String retailerId;
    private String processingOptionsActionCodes;

    // Constructors, getters, and setters
}


import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelService {

    public List<MerchantInfo> readExcel(String filePath) throws IOException {
        List<MerchantInfo> excelData = new ArrayList<>();

        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fileInputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // Assuming you are reading the first sheet

            // Assuming the first row contains headers
            Row headerRow = sheet.getRow(0);

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);

                MerchantInfo merchantInfo = new MerchantInfo();

                for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
                    Cell headerCell = headerRow.getCell(cellIndex);
                    Cell dataCell = row.getCell(cellIndex);

                    String header = headerCell.getStringCellValue();

                    switch (header) {
                        case "Merchant ID":
                            merchantInfo.setMerchantId(dataCell.getStringCellValue());
                            break;
                        case "Terminal ID":
                            merchantInfo.setTerminalId(dataCell.getStringCellValue());
                            break;
                        case "Description":
                            merchantInfo.setDescription(dataCell.getStringCellValue());
                            break;
                        case "Risk Profile ID":
                            merchantInfo.setRiskProfileId(dataCell.getStringCellValue());
                            break;
                        case "Payment Instrument":
                            merchantInfo.setPaymentInstrument(dataCell.getStringCellValue());
                            break;
                        case "Brand Name":
                            merchantInfo.setBrandName(dataCell.getStringCellValue());
                            break;
                        case "Currency":
                            merchantInfo.setCurrency(dataCell.getStringCellValue());
                            break;
                        case "Terminal Alias Name":
                            merchantInfo.setTerminalAliasName(dataCell.getStringCellValue());
                            break;
                        case "Tranportal ID":
                            merchantInfo.setTranportalId(dataCell.getStringCellValue());
                            break;
                        case "Tranportal Password":
                            merchantInfo.setTranportalPassword(dataCell.getStringCellValue());
                            break;
                        case "External Connection ID":
                            merchantInfo.setExternalConnectionId(dataCell.getStringCellValue());
                            break;
                        case "Card Acceptor ID":
                            merchantInfo.setCardAcceptorId(dataCell.getStringCellValue());
                            break;
                        case "Card Acceptor Terminal ID":
                            merchantInfo.setCardAcceptorTerminalId(dataCell.getStringCellValue());
                            break;
                        case "Retailer ID":
                            merchantInfo.setRetailerId(dataCell.getStringCellValue());
                            break;
                        case "Processing Options &Action Codes":
                            merchantInfo.setProcessingOptionsActionCodes(dataCell.getStringCellValue());
                            break;
                        // Add more cases if you have additional columns
                    }
                }

                excelData.add(merchantInfo);
            }
        }

        return excelData;
    }
}




