package com.example.paytransaction.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.paytransaction.dto.PaymentRequest;
import com.example.paytransaction.repo.ClientPaymentRequestDAO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentService {

	@Autowired
	private ClientPaymentRequestDAO clientPaymentRequestDAO;

	public PaymentResponseTransaction processPaymentRequest(PaymentRequest request) throws CustomException {
		PaymentResponseTransaction response = new PaymentResponseTransaction();
		try {
			// Stage 1: Save the payment request in DB (ClientPaymentRequestEntity)
			clientPaymentRequestDAO.saveClientPaymentRequestToDatabase(request);

			// Stage 2: Build Flexcube payload and send request
			PIDebitCreditRequest flexcubeRequest = createPayloadForFlexcubeFromClientRequest(request);
			String flexcubeResponse = callFlexcubeApiForPIDebitCredit(flexcubeRequest);

			// Stage 3: Process Flexcube response
			processFlexcubeApiResponse(flexcubeResponse, request, response);

			// Stage 4: Notify IBMB server about payment status
			notifyIbmbServerAboutPaymentStatus(request.getPayRequestTransaction());

		} catch (Exception e) {
			log.error("Error during payment processing", e);
			throw new CustomException(500, "Failed to process payment");
		}
		return response;
	}

}