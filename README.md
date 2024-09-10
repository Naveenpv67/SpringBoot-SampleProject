package com.example.helloworld.service;

import java.text.Normalizer;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.helloworld.model.DematHolding;

import jakarta.annotation.PostConstruct;

/**
 * Service class for managing Demat Holdings and performing search operations.
 */
@Service
public class DematHoldingService {

	private static final Logger logger = LoggerFactory.getLogger(DematHoldingService.class);
	private List<DematHolding> dematHoldingsList = new ArrayList<>();
	private Map<String, Double> similarityScores = new HashMap<>();

	/**
	 * Initializes the dematHoldingsList with test data.
	 */
	private static final String[] COMPANY_NAMES = { "HDFC ASSET MANAGEMENT COMPANY LIMITED-EQ NEW FV RS. 5/-",
			"HDFC LIFE INSURANCE COMPANY LIMITED-EQ", "RELIANCE INDUSTRIES LIMITED-EQ",
			"TATA CONSULTANCY SERVICES LIMITED-EQ", "INFOSYS LIMITED-EQ", "ICICI BANK LIMITED-EQ",
			"STATE BANK OF INDIA-EQ", "BHARTI AIRTEL LIMITED-EQ", "HINDUSTAN UNILEVER LIMITED-EQ",
			"KOTAK MAHINDRA BANK LIMITED-EQ", "LARSEN & TOUBRO LIMITED-EQ", "AXIS BANK LIMITED-EQ",
			"MARUTI SUZUKI INDIA LIMITED-EQ", "ITC LIMITED-EQ", "ASIAN PAINTS LIMITED-EQ",
			"SUN PHARMACEUTICAL INDUSTRIES LIMITED-EQ", "HCL TECHNOLOGIES LIMITED-EQ", "WIPRO LIMITED-EQ",
			"ULTRATECH CEMENT LIMITED-EQ", "NESTLE INDIA LIMITED-EQ", "BAJAJ FINANCE LIMITED-EQ",
			"TITAN COMPANY LIMITED-EQ", "POWER GRID CORPORATION OF INDIA LIMITED-EQ", "NTPC LIMITED-EQ",
			"TECH MAHINDRA LIMITED-EQ", "INDUSIND BANK LIMITED-EQ", "HINDALCO INDUSTRIES LIMITED-EQ",
			"JSW STEEL LIMITED-EQ", "TATA STEEL LIMITED-EQ", "ADANI PORTS AND SPECIAL ECONOMIC ZONE LIMITED-EQ",
			"COAL INDIA LIMITED-EQ", "BRITANNIA INDUSTRIES LIMITED-EQ", "SHREE CEMENT LIMITED-EQ",
			"GRASIM INDUSTRIES LIMITED-EQ", "ONGC LIMITED-EQ", "DR. REDDY'S LABORATORIES LIMITED-EQ",
			"CIPLA LIMITED-EQ", "DIVI'S LABORATORIES LIMITED-EQ", "BAJAJ AUTO LIMITED-EQ", "HERO MOTOCORP LIMITED-EQ",
			"EICHER MOTORS LIMITED-EQ", "MAHINDRA & MAHINDRA LIMITED-EQ", "TATA MOTORS LIMITED-EQ",
			"VEDANTA LIMITED-EQ", "ZEE ENTERTAINMENT ENTERPRISES LIMITED-EQ", "BHARAT PETROLEUM CORPORATION LIMITED-EQ",
			"INDIAN OIL CORPORATION LIMITED-EQ", "GAIL (INDIA) LIMITED-EQ",
			"OIL AND NATURAL GAS CORPORATION LIMITED-EQ", "ADANI GREEN ENERGY LIMITED-EQ",
			"ADANI TRANSMISSION LIMITED-EQ", "ADANI TOTAL GAS LIMITED-EQ", "BAJAJ HOLDINGS & INVESTMENT LIMITED-EQ",
			"BANDHAN BANK LIMITED-EQ", "BANK OF BARODA-EQ", "BERGER PAINTS INDIA LIMITED-EQ", "BIOCON LIMITED-EQ",
			"BOSCH LIMITED-EQ", "CADILA HEALTHCARE LIMITED-EQ", "CANARA BANK-EQ",
			"CHOLAMANDALAM INVESTMENT AND FINANCE COMPANY LIMITED-EQ",
			"CROMPTON GREAVES CONSUMER ELECTRICALS LIMITED-EQ", "DABUR INDIA LIMITED-EQ",
			"DIXON TECHNOLOGIES (INDIA) LIMITED-EQ", "DLF LIMITED-EQ", "ESCORTS LIMITED-EQ",
			"EXIDE INDUSTRIES LIMITED-EQ", "GODREJ CONSUMER PRODUCTS LIMITED-EQ", "GODREJ PROPERTIES LIMITED-EQ",
			"HAVELLS INDIA LIMITED-EQ", "HDFC BANK LIMITED-EQ", "HERO FINCORP LIMITED-EQ", "HINDUSTAN ZINC LIMITED-EQ",
			"ICICI LOMBARD GENERAL INSURANCE COMPANY LIMITED-EQ", "ICICI PRUDENTIAL LIFE INSURANCE COMPANY LIMITED-EQ",
			"IDFC FIRST BANK LIMITED-EQ", "INDIAN HOTELS COMPANY LIMITED-EQ", "INDRAPRASTHA GAS LIMITED-EQ",
			"INTERGLOBE AVIATION LIMITED-EQ", "IRCTC LIMITED-EQ", "JUBILANT FOODWORKS LIMITED-EQ",
			"LIC HOUSING FINANCE LIMITED-EQ", "LUPIN LIMITED-EQ", "M&M FINANCIAL SERVICES LIMITED-EQ",
			"MANAPPURAM FINANCE LIMITED-EQ", "MINDTREE LIMITED-EQ", "MOTHERSON SUMI SYSTEMS LIMITED-EQ",
			"MUTHOOT FINANCE LIMITED-EQ", "NATIONAL ALUMINIUM COMPANY LIMITED-EQ", "NAUKRI LIMITED-EQ",
			"NMDC LIMITED-EQ", "PAGE INDUSTRIES LIMITED-EQ", "PETRONET LNG LIMITED-EQ", "PI INDUSTRIES LIMITED-EQ",
			"PIRAMAL ENTERPRISES LIMITED-EQ", "PVR LIMITED-EQ", "RAJESH EXPORTS LIMITED-EQ", "RBL BANK LIMITED-EQ",
			"RELIANCE INFRASTRUCTURE LIMITED-EQ", "RELIANCE POWER LIMITED-EQ", "SAIL LIMITED-EQ", "SIEMENS LIMITED-EQ",
			"SRF LIMITED-EQ", "TATA CHEMICALS LIMITED-EQ", "TATA COMMUNICATIONS LIMITED-EQ",
			"TATA CONSUMER PRODUCTS LIMITED-EQ", "TATA ELXSI LIMITED-EQ", "TATA POWER COMPANY LIMITED-EQ",
			"TATA PROJECTS LIMITED-EQ", "TATA REALTY AND INFRASTRUCTURE LIMITED-EQ", "TATA TECHNOLOGIES LIMITED-EQ",
			"TATA TELECOM LIMITED-EQ", "TORRENT PHARMACEUTICALS LIMITED-EQ", "ABC XYZ TECHX", "TECHX ABC XYZ", "ABC TECHX XYZ",
			// Add more company names here to reach 500
	};

	@PostConstruct
	public void init() {
		for (int i = 0; i < COMPANY_NAMES.length; i++) {
			DematHolding holding = new DematHolding();
			holding.setSrno(String.valueOf(i + 1));
			holding.setCompanyName(COMPANY_NAMES[i]);
			holding.setIsin("INE" + String.format("%09d", i + 1));
			holding.setBalanceStatus("Free");
			holding.setCategory("EQ");
			holding.setBalanceQty(String.valueOf((i + 1) * 10));
			holding.setMarketRate(String.valueOf((i + 1) * 100));
			holding.setMarketValue(String.valueOf((i + 1) * 1000));
			holding.setValdttime("08 Sep, 08:00 PM");
			holding.setMkttp("0");
			holding.setLockindt("");
			holding.setLockrsn("");
			holding.setAcctdesc("Free Balance");
			dematHoldingsList.add(holding);
		}
		logger.info("Demat holdings initialized with " + dematHoldingsList.size() + " records.");
	}

	/**
	 * Searches for Demat Holdings by a specified field and query.
	 *
	 * @param field the field to search by (e.g., "companyname", "isin")
	 * @param query the search query
	 * @return a list of matching Demat Holdings
	 * @throws IllegalArgumentException if the query length is less than 3
	 *                                  characters
	 */
	public List<DematHolding> searchByField(String field, String query) {
	    if (query == null || query.isEmpty()) {
	        return new ArrayList<>();
	    }

	    if (query.length() < 3) {
	        throw new IllegalArgumentException("Query length must be at least 3 characters.");
	    }

	    String lowerCaseQuery = normalize(query.toLowerCase());
	    String[] keywords = lowerCaseQuery.split("\\s+");

	    return dematHoldingsList.stream()
	        .filter(holding -> {
	            Optional<String> fieldValueOpt = getFieldValue(holding, field);
	            if (!fieldValueOpt.isPresent()) return false;

	            String fieldValue = fieldValueOpt.get();
	            boolean match = containsAllKeywords(fieldValue, keywords) || isFuzzyMatch(fieldValue, lowerCaseQuery);
	            if (match) {
	                double similarity = calculateSimilarity(fieldValue, lowerCaseQuery);
	                similarityScores.put(fieldValue, similarity);
	            }
	            return match;
	        })
	        .sorted((h1, h2) -> {
	            String fieldValue1 = getFieldValue(h1, field).orElse("");
	            String fieldValue2 = getFieldValue(h2, field).orElse("");
	            boolean startsWith1 = fieldValue1.startsWith(lowerCaseQuery);
	            boolean startsWith2 = fieldValue2.startsWith(lowerCaseQuery);
	            if (startsWith1 && !startsWith2) return -1;
	            if (!startsWith1 && startsWith2) return 1;
	            return Double.compare(similarityScores.getOrDefault(fieldValue2, 0.0), similarityScores.getOrDefault(fieldValue1, 0.0));
	        })
	        .collect(Collectors.toList());
	}


	/**
	 * Retrieves the normalized value of a specified field from a Demat Holding.
	 *
	 * @param holding the Demat Holding
	 * @param field   the field to retrieve (e.g., "companyname", "isin")
	 * @return an Optional containing the normalized field value, or an empty
	 *         Optional if the field is invalid
	 */
	private Optional<String> getFieldValue(DematHolding holding, String field) {
		try {
			switch (field.toLowerCase()) {
			case "companyname":
				return Optional.of(normalize(holding.getCompanyName().toLowerCase()));
			case "isin":
				return Optional.of(normalize(holding.getIsin().toLowerCase()));
			default:
				return Optional.empty();
			}
		} catch (Exception e) {
			logger.error("Error normalizing field value for field: {}", field, e);
			return Optional.empty();
		}
	}

	/**
	 * Checks if all keywords are contained within a field value.
	 *
	 * @param fieldValue the field value to check
	 * @param keywords   the keywords to check for
	 * @return true if all keywords are contained within the field value, false
	 *         otherwise
	 */
	private boolean containsAllKeywords(String fieldValue, String[] keywords) {
		return Arrays.stream(keywords).allMatch(fieldValue::contains);
	}

	/**
	 * Performs a fuzzy match between a field value and a query using Levenshtein
	 * distance.
	 *
	 * @param fieldValue the field value to compare
	 * @param query      the query to compare
	 * @return true if the similarity is above the threshold, false otherwise
	 */
	private boolean isFuzzyMatch(String fieldValue, String query) {
		try {
			LevenshteinDistance levenshtein = new LevenshteinDistance();
			int distance = levenshtein.apply(fieldValue, query);
			int maxLength = Math.max(fieldValue.length(), query.length());
			double similarity = 1.0 - (double) distance / maxLength;

			// Log the details only if necessary
			if (logger.isDebugEnabled()) {
				logger.debug("Comparing '{}' with '{}'", fieldValue, query);
				logger.debug("Levenshtein distance: {}", distance);
				logger.debug("Similarity: {}", similarity);
			}

			return similarity >= 0.7; // Adjust this threshold as needed
		} catch (Exception e) {
			logger.error("Error calculating fuzzy match for field value: {}", fieldValue, e);
			return false;
		}
	}

	/**
	 * Calculates the similarity between a field value and a query using Levenshtein
	 * distance.
	 *
	 * @param fieldValue the field value to compare
	 * @param query      the query to compare
	 * @return the similarity score
	 */
	private double calculateSimilarity(String fieldValue, String query) {
		try {
			LevenshteinDistance levenshtein = new LevenshteinDistance();
			int distance = levenshtein.apply(fieldValue, query);
			int maxLength = Math.max(fieldValue.length(), query.length());
			return 1.0 - (double) distance / maxLength;
		} catch (Exception e) {
			logger.error("Error calculating similarity for field value: {}", fieldValue, e);
			return 0.0;
		}
	}

	/**
	 * Normalizes a string by removing diacritical marks and non-ASCII characters.
	 *
	 * @param input the input string to normalize
	 * @return the normalized string
	 */
	private String normalize(String input) {
		try {
			return Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{M}", "").replaceAll("[^\\p{ASCII}]",
					"");
		} catch (Exception e) {
			logger.error("Error normalizing input: {}", input, e);
			return input;
		}
	}

	/**
	 * Retrieves the similarity scores sorted in descending order.
	 *
	 * @return a list of entries containing field values and their similarity scores
	 */
	public List<Map.Entry<String, Double>> getSortedSimilarityScores() {
		try {
			return similarityScores.entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed())
					.collect(Collectors.toList());
		} catch (Exception e) {
			logger.error("Error sorting similarity scores", e);
			return new ArrayList<>();
		}
	}
}
