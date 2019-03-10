package in.enzen.taskforum.rest;

/**
 * Created by Rupesh on 05-01-2018.
 */
@SuppressWarnings("ALL")
public interface BaseUrl {

    /*----  Live URL API  ----*/
    String sBaseURL = "http://semicolonindia.in/workforce/mobile_api/";

    /*----  Testing URL API  ----*/
    // String sBaseURL = "http://semicolonindia.in/work-force-new/mobile_api/";


    // String sBaseURL = "http://newdemo.com/mobile_api/";
    // String sBaseURL = "http://192.168.43.236/";
    public String sSaveBillCollection = sBaseURL + "save_bill_collection";
    // public String sLocationUpdate = sBaseURL + "save_live_location";

    /*----  Other API'S Here  ----*/
    public String sLoginURL = sBaseURL + "user/login";
    public String sLocationUpdate = sBaseURL + "user/saveLocation";
    public String sCollectionTarget = sBaseURL + "collection/saveCollection";
    public String sDisconnectionTarget = sBaseURL + "disconnection/saveDisconnection";
    public String sDisconnectionSquad = sBaseURL + "disconnectionsquad/saveDisconnectionSquad";
    public String sArrearDriveAll = sBaseURL + "arrear/saveArrearDrive";
    public String sDTSanitisation = sBaseURL + "DTSanitisation/saveDTSanitisation";
    public String sProfileUpdate = sBaseURL + "user/profileUpdate";
    public String sNotification = sBaseURL + "notification/getNotification";
    public String sSavePreventive = sBaseURL + "preventive/savePreventive";
    public String sDehooking = sBaseURL + "dehooking/saveDehooking";
    public String sCheckMeter = sBaseURL + "meter/checkMeterReading";
    public String sTemporaryConnection = sBaseURL + "connection/saveTempConnection";
    public String sNewConnection = sBaseURL + "connection/saveNewConnection";

    public String sAssignmentTarget = sBaseURL + "reports/getUserTarget";

    public String sMeterReading = sBaseURL + "meterReading/saveMeterReading";
    public String sMeetingCOM = sBaseURL + "staff/saveMeetingJM_COM";
    public String sVillageMeetingAMC = sBaseURL + "consumer/saveVillageMeeting";
    public String sConsumerMella = sBaseURL + "consumer/saveConsumerMella";
    public String sBillRevision = sBaseURL + "bill/billRevised";
    public String sMeetingMRT = sBaseURL + "staff/saveMeetingJM_MRT";
    public String sMeetingOnM = sBaseURL + "staff/saveMeetingJM_OM";
    public String sCurrentBill = sBaseURL + "bill/currentBill";
    public String sInstallVerification = sBaseURL + "install/installVerification";

    public String sReportPieChart = sBaseURL + "dashboard/getuserdashboard";
    public String sReportFilterList = sBaseURL + "dashboard/getFilterList";

    public String sSdoStaffMeeting = sBaseURL + "staff/saveMeetingSDO_AMC";

    public String sAuditReporting = sBaseURL + "sdoamc/energyAuditReporting";
    public String sSDOAssessment = sBaseURL + "sdoamc/saveAssesment";
    public String sSDORecoveryAssesment = sBaseURL + "sdoamc/recoveryAssesment";

    public String sNoOfRC = sBaseURL + "jmComRev/noOfRc";
    public String sDisconnection1Phase = sBaseURL + "jmComRev/noDisconnection";
    public String sCollectionAgainstRC = sBaseURL + "jmComRev/arrearCollectionAgnstRc";
    public String sUnauthorisedConsumer = sBaseURL + "jmComRev/idUnauthorConsumer";
    public String sVillageFormation = sBaseURL + "jmComRev/formationVlgCommitte";
    public String sDayTotalCollectionAmount = sBaseURL + "jmComRev/daysTtlCollAmt";
    public String sComplaintReceived = sBaseURL + "jmComRev/commercialComplaintRcv";
    public String sComBillRevision = sBaseURL + "jmComRev/recoveryFrmBillRevisCases";
    public String sInstallVerificationCOM = sBaseURL + "install/installVerificationJM_COM";

    public String sMeterDefective = sBaseURL + "jmMartRev/noMtrFoundDf";
    public String sTemperCases = sBaseURL + "jmMartRev/noBypassTemperCasesId";
    public String sSquadOperation = sBaseURL + "jmMartRev/jointSqureOprn";
    public String sNewCharging = sBaseURL + "charging/saveNewCharging";

    public String sDTREnergyAudit = sBaseURL + "jmOmRev/dtrEnergyAudit";
    public String sCheckMeterReading = sBaseURL + "jmOmRev/checkMeterReading";
    public String sPoleReplacement = sBaseURL + "jmOmRev/poleReplacement";
    public String sUnauthorisedConsumerOM = sBaseURL + "jmOmRev/idUnauthorConsumer";
    public String sVillageCommitte = sBaseURL + "jmOmRev/villageCommitte";
    public String sLineSpacer = sBaseURL + "jmOmRev/lineSpacer";
    public String sSafetyWork = sBaseURL + "jmOmRev/safetyWork";
    public String sDTFencing = sBaseURL + "jmOmRev/dtFencing";
    public String sDailyInputReading = sBaseURL + "jmOmRev/dailyInputReading";
    public String sElectricAccident = sBaseURL + "jmOmRev/occurElectricAccident";
    public String sTreeTrimming = sBaseURL + "treeTrimming/saveTreeTrimming";

    public String sGRF = sBaseURL + "grf/saveGrf";
    public String sEnforcement = sBaseURL + "enforcement/saveEnforcement";


    // CHAT API List
    public String sChatLists = sBaseURL + "chatting";
    // CHAT API Messages Conversation
    public String sChatDetails = sBaseURL + "chatting/getChatDetails";
    // CHAT API --> Send Messeges to other Users
    public String sSendMessage = sBaseURL + "chatting/saveChatDetails";


}
