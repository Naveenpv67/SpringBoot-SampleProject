Hi I have a technical clarification regarding the integration of the MFA Status Update API.(BioCatch/PRM integration)

For the BioCatch MFA Status API:
Should we report every single failure, or only the final Success/3rd-failure?
I suspect reporting every attempt is better for their behavioral analysis and anomaly detection.
Please confirm the preferred approach.

Since BioCatch is a behavioral biometrics tool, they usually care more about how a user interacts with the system than just the final result.      
