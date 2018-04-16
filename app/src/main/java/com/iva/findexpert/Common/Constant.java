package com.iva.findexpert.Common;

import java.sql.Struct;

/**
 * Created by LENOVO on 10/23/2016.
 */

public class Constant {

    public static final String APP_KEY = "com.iva.FindExpert";
    public static final String BASE_URL = "https://api.solk.lk";
    //public static final String BASE_URL = "http://192.168.8.100/SOLK";

    public static final int PHONE_NUMBER_LENGTH = 10;
    public static int REQUEST_TIME_OUT = 20; //20 secs
    public static int REQUEST_RETRIES = 0; //20 secs

    public static final int MODULE_BUYER_HOME = 0;
    public static final int MODULE_BUYER_PROFILE_ = 1;
    public static final int MODULE_BUYER_PRODUCTS_OFFERS = 2;
    public static final int MODULE_BUYER_HELP = 3;
    public static final int MODULE_BUYER_FEEDBACK = 4;
    public static final int MODULE_BUYER_NOTIFICATIONS = 5;
    public static final int MODULE_BUYER_LOG_OFF = 7;

    public static final int MODULE_SELLER_HOME = 0;
    public static final int MODULE_SELLER_PROFILE_ = 1;
    public static final int MODULE_SELLER_PRODUCTS_OFFERS = 2;
    public static final int MODULE_SELLER_HELP = 3;
    public static final int MODULE_SELLER_FEEDBACK = 4;
    public static final int MODULE_SELLER_NOTIFICATIONS = 5;
    public static final int MODULE_SELLER_LOG_OFF = 7;

    public static final String CONTROLLER_BUYER = "/api/Buyer";
    public static final String CONTROLLER_SELLER = "/api/Seller";
    public static final String CONTROLLER_SERVICE_REQUEST = "/api/ServiceRequest";
    public static final String CONTROLLER_QUOTATION = "/api/Quotation";
    public static final String CONTROLLER_USER = "/api/User";
    public static final String CONTROLLER_MESSAGE = "/api/Message";
    public static final String CONTROLLER_TEMPLATE = "/api/QuotationTemplate";
    public static final String CONTROLLER_PROMOTION = "/api/Promotion";
    public static final String CONTROLLER_AROUND_ME = "/api/AroundMe";
    public static final String CONTROLLER_NOTIFICATION = "/api/Notification";

    public static final String SERVICE_PHONE_VALIDATE = "/PhoneValidate";
    public static final String SERVICE_CODE_VALIDATE = "/CodeValidate";
    public static final String SERVICE_AGENT_VALIDATE = "/SellerValidate";
    public static final String SERVICE_SR_SAVE = "/Save";
    public static final String SERVICE_SR_BUYER = "/GetByBuyerId";
    public static final String SERVICE_SR_AGENT = "/GetByAgentId";
    public static final String SERVICE_SR_AGENT_PENDING = "/GetPendingByAgentId";
    public static final String SERVICE_SR_AGENT_FOLLOWUP = "/GetFollowUpByAgentId";
    public static final String SERVICE_SR_GET = "/GetById";
    public static final String SERVICE_SR_GET_BY_QUOTE_ID = "/GetByQuoteId";
    //public static final String SERVICE_QUOTATION_GET = "/GetById";
    public static final String SERVICE_QUOTATION_SEND = "/Save";
    public static final String SERVICE_QUOTATION_ACCEPT = "/Accept";
    public static final String SERVICE_QUOTATION_ACCEPT_MTHREAD = "/AcceptByMessageThread";
    public static final String SERVICE_GET_PROFILE = "/GetProfileByUserId";
    public static final String SERVICE_UPDATE_PROFILE = "/UpdateProfile";
    public static final String SERVICE_FEEDBACK = "/FeedBack";
    public static final String SERVICE_GET_BUYER_THREADS = "/GetBuyerThreads";
    public static final String SERVICE_GET_AGENT_THREADS = "/GetAgentThreads";
    public static final String SERVICE_GET_THREAD = "/GetThreadById";
    public static final String SERVICE_GET_MESSAGE_COUNT = "/UnreadMessageCount";
    public static final String SERVICE_GET_PENDING_COUNT = "/PendingRequestCount";
    public static final String SERVICE_GET_FOLLOWUP_COUNT = "/GetFollowUpByAgentCount";
    public static final String SERVICE_ADD_MESSAGE = "/AddMessage";
    public static final String SERVICE_TEMPLATE_GET = "/GetTemplateById";
    public static final String SERVICE_TEMPLATE_GET_BY_AGENT = "/GetTemplateByAgent";
    public static final String SERVICE_TEMPLATE_UPDATE = "/Update";
    public static final String SERVICE_TEMPLATE_ADD = "/Add";
    public static final String SERVICE_GET_PROMOTIONS = "/GetPromotions";
    public static final String SERVICE_GET_PROMOTION = "/GetPromotionById";
    public static final String SERVICE_GET_LATEST_PROMOTION = "/GetLatestPromotion";
    public static final String SERVICE_GET_SERVICE_CATEGORIES = "/GetServiceCategories";
    public static final String SERVICE_GET_SERVICE_PROVIDERS = "/GetServiceProviders";
    public static final String SERVICE_GET_NOTIFICATIONS = "/GetNotifications";
    public static final String SERVICE_GET_NOTIFICATION_LIST = "/GetNotificationsForList";
    public static final String SERVICE_DELETE_SELECTED_NOTIFICATIONS = "/Delete";
    public static final String SERVICE_DELETE_ALL_NOTIFICATIONS = "/DeleteAll";

    public static final String SERVICE_USER_DEVICE = "/AddUserDevice";
    public static final String SERVICE_GET_BUYER_FOLLOWUP_COUNT = "/GetFollowUpByBuyerCount";
    public static final String SERVICE_GET_QUOTATION_FROM_BUYER = "/GetByIdFromBuyer";
    public static final String SERVICE_GET_QUOTATION_BY_ID = "/GetById";

    public static final int SERVICE_NOTIFY_INTERVAL = 30000;

    public static  class UserType
    {
        public static final int BUYER = 1;
        public static final int SELLER = 2;
    }

    public static class HttpStatus
    {
        public static final int OK = 200;
        public static final int UNAUTHORIZED = 401;
        public static final int PAGE_NOT_FOUND = 400;
    }

    public static class ClaimType
    {
        public static final int COMPREHENSIVE = 1;
        public static final int THIRD_PARTY = 2;
    }

    public static class ClaimTypeName
    {
        public static final String COMPREHENSIVE = "Comprehensive";
        public static final String THIRD_PARTY = "Third Party";
    }

    public static class Usage
    {
        public static final int PRIVATE = 1;
        public static final int HIRING = 2;
        public static final int RENT = 3;

        public static final String PRIVATE_NAME = "Private";
        public static final String HIRING_NAME = "Hiring";
        public static final String RENT_NAME = "Rent";
    }

    public static class FuelType
    {
        public static final int GAS = 1;
        public  static final int HYBRID = 2;
        public static final int ELECTRIC = 3;
    }

    public static class SessionKeys
    {
        public static final String USER_PHONE = "SESSION_USER_PHONE";
        public static final String AGENT_USER_PHONE = "SESSION_AGENT_PHONE";
        public static final String USER_ID = "SESSION_USER_ID";
        public static final String REQUEST_ID = "REQUEST_ID";
        public static final String QUOTATION_ID = "QUOTATION_ID";
        public static final String INSURANCE_TYPE = "SESSION_INSURANCE_TYPE";
        public static final String MESSAGE_THREAD_ID = "MESSAGE_THREAD_ID";
        public static final String RECIPIENT_ID = "AGENT_ID";
        public static final String RECIPIENT_NAME = "AGENT_NAME";
        public static final String AGENT_LIST_FOCUS_KEY = "AGENT_LIST_FOCUS_KEY";
        public static final String TEMPLATE_ID = "TEMPLATE_ID";
        public static final String TEMPLATE_OBJECT = "TEMPLATE_OBJECT";
        public static final String PROMOTION_ID = "PROMOTION_ID";
        public static final String REQUEST_LIST_FOCUS = "REQUEST_LIST_FOCUS";
        public static final String APP_FIRST_RUN = "APP_FIRST_RUN";
        public static final String CATEGORY_ID = "CATEGORY_ID";
        public static final String CATEGORY_NAME = "CATEGORY_NAME";
        public static final String LAST_PROMOTION = "LAST_PROMOTION";
        public static final String LAST_SELECTED_SERVICE = "LAST_SELECTED_SERVICE";
        public static final String SERVICE_REQUEST = "SERVICE_REQUEST";
        public static final String PARENT_UI = "PARENT_UI";

    }

    public static class ParentUIName
    {
        public static final String FROM_HOME = "FROM_HOME";
        public static final String FROM_INQUIRY_LIST = "FROM_INQUIRY_LIST";
        public static final String FROM_MESSAGE_LIST = "MESSAGE_LIST";
    }

    public static class QuotaionRequestStatus
    {
        public static final int INITIAL = 1;
        public static final int PENDING = 2;
        public static final int RESPONDED = 3;
        public static final int EXPIRED = 4;
        public static final int CLOSED = 5;
    }

    public static class QuotationStatus
    {
        public static final int NONE = 0;
        public static final int INITIAL = 1;
        public static final int ACCEPTED = 2;
        public static final int CLOSED = 3;
        public static final int CHECKED = 4;
    }

    public  static  class AgentRequestListFocus
    {
        public static final int ALL = 1;
        public static final int PENDING = 2;
        public static final int FOLLOWUP = 3;
    }

    public static class RequestStatus
    {
        public static final int ALL = 1;
        public static final int PENDING = 2;
        public static final int CLOSED = 5;
        public static final int EXPIRED = 4;
    }

    public static class NotificationType
    {
        public static final String NAME = "TYPE_NAME";
        public static final String ID = "TYPE_ID";
        public static final int REQUEST = 1;
        public static final int MESSAGE = 2;
        public static final int QUOTATION = 3;
        public static final int ACCEPT = 4;
        public static final int FOLLOWUP_BUYER = 5;
        public static final int FOLLOWUP_SELLER = 6;
    }

    public static class Notification
    {
        public static final String REFRESH_MESSAGE_COUNT = "REFRESH_MESSAGE_COUNT";
    }

}
