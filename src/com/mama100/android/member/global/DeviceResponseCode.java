package com.mama100.android.member.global;

/**
 * 应答码
 * @author jimmy
 */
public class DeviceResponseCode {

	// 成功
	public static final String SUCCESS = "100";
	
	// 消息来源不合法
	public static final String INVALID_MSG_SOURCE = "6001";
	
	// 操作系统格式不正确
	public static final String INVALID_OS = "6002";

	// 操作系统格式不正确
	public static final String INVALID_OS_VER = "6003";

	// 设备ID格式不正确
	public static final String INVALID_DEVICE_ID = "6004";
	
	// 品牌格式不正确
	public static final String INVALID_BRAND = "6005";

	// 型号格式不正确
	public static final String INVALID_MODEL = "6006";

	// 客户端内部版本号格式不正确
	public static final String INVALID_APP_VER = "6007";
	
	// 客户端版本号格式不正确
	public static final String INVALID_APP_VER_NAME = "6008";
	
	// 屏幕像素宽格式不正确
	public static final String INVALID_SCREEN_WIDTH = "6009";

	// 屏幕像素高格式不正确
	public static final String INVALID_SCREEN_HIGHT = "6010";

	// 屏幕密度格式不正确
	public static final String INVALID_SCREEN_DPI = "6011";

	// 全局密钥版本号格式不正确
	public static final String INVALID_GLOBAL_KEY_VERSION = "6012";	
	
	// 工作密钥版本号格式不正确
	public static final String INVALID_WORK_KEY_VERSION = "6013";
	
	// 工作密钥验证数据格式不正确
	public static final String INVALID_DATA_BY_WORK_KEY = "6014";
	// 渠道号不正确
		public static final String INVALID_CHANNEL = "6015";

	
	
	// 会员店编号输入错误
	public static final String INVALID_TERMINAL_CODE = "122";
	
	// 会员卡号输入错误
	public static final String INVALID_CARD_NO = "113";
	
	// 会员卡密码输入错误
	public static final String INVALID_CARD_PWD = "114";
	
	// 手机号码输入错误
	public static final String INVALID_MOBILE = "115";

	// 产品序列号输入错误
	public static final String INVALID_PRODUCT_SERIAL_CODE = "119";
	
	// 产品编码输入错误
	public static final String INVALID_PRODUCT_CODE = "120";
	
	// 产品防伪码输入错误
	public static final String INVALID_PRODUCT_SECURITY_CODE = "121";

	// 固定电话输入错误
	public static final String INVALID_PHONE = "123";

	// 卡激活码输入错误
	public static final String INVALID_CARD_ACTIVE_CODE = "124";

	// 卡号,手机和电话必填一项
	public static final String CARD_MOBILE_PHONE_NEED_ONE = "125";
	
	// 卡号,手机必填一项
	public static final String CARD_MOBILE_NEED_ONE = "126";

	// 促销员帐号不正确
	public static final String INVALID_SALES_NO = "127";

	// 促销员密码不正确
	public static final String INVALID_SALES_PWD = "128";
	
	// 登录密码格式不正确
	public static final String INVALID_LOGIN_PWD = "129";

	// 操作员编号输入错误
	public static final String INVALID_OPER_NO = "130";

	// 操作员密码输入错误
	public static final String INVALID_OPER_PWD = "131";
	
	// 旧密码不正确
	public static final String INVALID_OLD_PWD = "141";
	
	// 新密码不正确
	public static final String INVALID_NEW_PWD = "142";
	
	// 兑换单号输入错误
	public static final String INVALID_EXG_ORDER_NO = "149";
	
	// 日期格式输入错误
	public static final String INVALID_DATE = "150";
	
	// 出生日期或预产期格式不正确
	public static final String INVALID_BIRTHDAY = "151";	
	
	// 抽奖期数输入错误
	public static final String INVALID_AWARD_TERM = "159";	
	
	// 数据校验失败
	public static final String VALIDATE_ERROR = "161";	
	
	
	// =================== 系统底层应答码 ===================

	
	// 此设备已被停用
	public static final String DEVICE_STOPED = "6101";
	
	// 没有发现新的版本
	public static final String NEW_VER_NOT_FOUND = "6102";

	
	// 主密钥版本号太低
	public static final String MAIN_KEY_VER_LOW = "150";

	// 主密钥不正确
	public static final String INVALID_MAIN_KEY = "151";

	// 主密钥未找到或未分配
	public static final String MAIN_KEY_NOT_FOUND = "152";

	// 工作密钥版本号太低
	public static final String WORK_KEY_VER_LOW = "153";

	// 工作密钥不正确
	public static final String INVALID_WORK_KEY = "154";

	// 工作密钥未找到或未分配
	public static final String WORK_KEY_NOT_FOUND = "155";

	// 消息记录不存在
	public static final String MSG_NOT_EXIST = "159";
	
	// 活动不存在
	public static final String ACTIVITY_NOT_EXIST = "160";
	
	
	// 登录帐号不正确
	public static final String LOGIN_ACCOUNT_ERROR = "257";

	// 登录密码不正确
	public static final String LOGIN_PWD_ERROR = "258";

	// 操作员密码不正确
	public static final String OPER_PWD_ERROR = "260";
	
	// 促销员帐号不正确
	public static final String SALES_ACCOUNT_ERROR = "261";

	// 促销员密码不正确
	public static final String SALES_PWD_ERROR = "262";	

	// 终端不存在
	public static final String TERMINAL_NOT_EXIST = "333";	
	
	// 此产品不能兑换
	public static final String PRODUCT_CANNOT_EXG = "334";
	
	// CRM终端已取消
	public static final String TERMINAL_STATUS_ABNORMAL = "335";	
	
	// 登录帐号未启用
	public static final String LOGIN_ACCOUNT_NOT_ENABLED = "336";
	
	// =================== 用户交互应答码 ===================
	
	// 卡号不存在
	public static final String CARD_NO_NOT_EXIST = "201";
	
	// 卡号非正常状态
	public static final String CARD_NO_ABNORMAL = "202";
	
	// 交易密码不正确
	public static final String TRA_PWD_ERROR = "203";

	// 旧密码不正确
	public static final String OLD_CARD_PWD_ERROR = "204";

	// 积分余额不足
	public static final String POINT_BALANCE_LOW = "205";
	
	// 产品序列号错误
	public static final String PRODUCT_SERIA_CODE_ERROR = "206";
	
	// 产品未登记
	public static final String PRODUCT_NOT_EXIST = "208";
	
	// 产品序列号重复
	public static final String PRODUCT_SERIA_CODE_REPEAT = "209";

	// 产品防伪码重复
	public static final String PRODUCT_SECURITY_CODE_REPEAT = "210";
	
	// 没有促销活动
	public static final String NO_ACTIVITY = "215";
	
	// 没有发布的信息
	public static final String NO_INFO = "216";
	
	// 没有可更新的任务
	public static final String NO_UPDATED_TASK = "217";
	
	// 卡曾经已发
	public static final String CARD_ALREADY_SENDED = "221";

	// 妈妈100账户不存在
	public static final String MAMA100_ACCOUNT_NOT_EXIST = "226";

	// 会员账户不存在
	public static final String MEMBER_ACCOUNT_NOT_EXIST = "227";
	
	// 条码未输入,不能领取
	public static final String ACT_PRODUCT_CODE_NOT_EXIST = "228";
	
	// 没有查询到任何数据
	public static final String NO_DATA_FOUND = "229";
	
	// 活动参与次数已满,不能再参与
	public static final String ACT_NUM_TOP = "230";
	
	// 会员初始密码未设置
	public static final String CARD_PWD_NOT_SET = "231";
	
	// 不是合生元会员
	public static final String NOT_BIO_MEMEBER = "232";
	
	// 会员账户非正常状态
	public static final String ACCOUNT_ABNORMAL = "233";
	
	// 卡曾经已激活
	public static final String CARD_ALREADY_ACTIVED = "234";

	// 卡未预设激活码
	public static final String CARD_NOT_SET_ACTIVE_CODE = "235";

	// 促销活动不存在
	public static final String SALE_RULE_NOT_FOUND = "237";
	
	// 卡号未关联
	public static final String CARD_NO_NOT_ASSO = "240";
	
	// 礼品曾今已发给该会员,不能再次领取
	public static final String GIFT_HAS_SENDTO_MEMBER = "241";
	
	// 已兑换产品不能积分
	public static final String CANNOT_REG_DUE_TO_EXCHANGED = "243";
	
	// 产品不能重复兑换
	public static final String REPEAT_EXCHANGE = "244";

	// 会员等级不符
	public static final String MEMBER_RANK_NOT_MATCH = "245";

	// 宝宝月龄不符合要求
	public static final String BABY_MONTH_NOT_MATCH = "246";
	
	// 卡未激活
	public static final String CARD_NOT_ACTIVE = "247";
	
	// 已积分产品不能兑换
	public static final String CANNOT_EXG_DUE_TO_REGED = "250";
	
	// 已积分产品不能再次积分
	public static final String CANNOT_REG_DUE_TO_REGED = "251";
	
	// 该账户不存在,按确定添加新会员
	public static final String MEMBER_ACCOUNT_NOT_EXIST_4_REG = "252";
	
	// 卡未发
	public static final String CARD_NOT_SEND = "253";
	
	// 此卡曾今已挂失
	public static final String CARD_LOSTED = "254";
	
	// 此卡曾今已销户
	public static final String CARD_CANCELLED = "255";
	
	// 此卡未挂失
	public static final String CARD_NOT_LOSTED = "256";
	
	// 客服系统新加会员失败
	public static final String CREATE_MEMBER_FAILURE = "300";
	
	// 此手机号已关联了卡号，不能再次关联
	public static final String USER_HAS_ASSO_CARD = "301";
	
	// 还有几期杂志未领
	public static final String MAG_HAS_MORE = "312";	
	
	// 您订阅的杂志已经领完,不能再领
	public static final String MAG_NO_MORE = "313";
	
	// 已经领过一次   购买产品并积分后，即可免费订阅和领取杂志
	public static final String MAG_HAS_GET = "314";
	
	// 积分或兑换的消息结果失败
	public static final String REG_OR_EXG_MSG_FAILED = "315";
	
	// 资料不完整，请拨打客服电话获取激活码（新会员）
	public static final String NEW_MEMBER_CANNOT_GET_ACODE = "316";
	
	// 资料不完整，请拨打客服电话获取激活码（地址不完整）
	public static final String NEW_ADDR_CANNOT_GET_ACODE = "317";
	
	// 会员资料不存在,请联系客服
	public static final String MEMBER_NOT_IN_CRM = "318";	
	
	// 婴线终端才可以订阅杂志
	public static final String SHOP_CAN_DESCRIBE_MAG = "319";
	
	// 婴线终端才可以领取杂志
	public static final String SHOP_CAN_GET_MAG = "320";
	
	// 没有未处理的订单
	public static final String NO_ORDER_LIST = "321";
	
	// 订单不存在
	public static final String ORDER_NO_EXIST = "322";
	
	// 收货人信息不存在
	public static final String RECEIVER_INFO_NO_EXIST = "323";
	
	// 某些兑换的积分产品没有输入序列号和防伪码
	public static final String ORDER_ITEM_NUM_NO_END = "324";
	
	// 兑换单的序列号和防伪码格式不正确
	public static final String ORDER_P_S_NO_ERROR = "325";
	
	// 此兑换订单不是该门店所有
	public static final String ORDER_TCD_NO_EQL = "326";
	
	// 此兑换订单已经处理过
	public static final String ORDER_HAS_HANDLED = "327";
	
	// 不符合领取条件，三个月内有积分才可免费领取杂志
	public static final String MAG_NO_REG_POINT = "328";	
	
	// 兑换单处理失败
	public static final String ORDER_HANDLE_FAILED = "329";
	
	// 产品条码未设置
	public static final String PRODUCT_BARCODE_NO_REG = "330";
	
	// 兑换单处理失败  积分额不正确
	public static final String ORDER_POINT_NO_EQ = "331";
	
	// 暂没有积分明细
	public static final String REG_DETAIL_NO_FOUND = "332";
	
	
	// CRM终端不存在
	public static final String CRM_TERMINAL_NOT_EXIST = "333";	

	
	// CRM终端已取消
	public static final String CRM_TERMINAL_STATUS_ABNORMAL = "335";	

	// 杂志配送查询输入月份不正确
	public static final String MAG_DEL_MON_ERROR = "337";
	
	// 暂无记录
	public static final String MAG_DEL_NO_REC = "338";
	
	// 只能作废纸尿裤超惠装产品
	public static final String CANCEL_SERIAL_NUM_NO_PRODUCT = "339";
	
	//不能作废重复序列号
	public static final String CANCEL_SERIAL_NUM_REPEAT = "340";

	
	// =================== 活动提示信息 ==================
	
	// 无效的活动ID
	public static final String INVALID_ACT_ID = "401";
	
	// 无效的活动类型
	public static final String INVALID_ACT_TYPE = "402";
	
	// 屁屁球活动码无效
	public static final String PIPI_CODE_NO_EXIST = "403";
	
	// 屁屁球活动码无效
	public static final String PIPI_CODE_ALREADY_REG = "404";
	
	// 暂无最新的活动信息
	public static final String ACT_LIST_NOT_FOUND = "405";

	// 活动不存在
	public static final String ACT_NOT_FOUND = "406";
	
	// 无效的活动编码
	public static final String INVALID_ACT_CODE = "407";	
	
	// 此会员已登记过屁屁球活动码
	public static final String PIPI_USER_ALREADY_REG = "409";
	
	// 此会员未登记过屁屁球活动码
	public static final String PIPI_USER_NO_REG = "410";
	
	// 手机号码与活动注册时登记的不一样
	public static final String PIPI_USER_MOBILE_NO_EQU = "411";
	
	// 必须在礼品短信内容中提到的会员店领取
	public static final String PIPI_USER_TCD_NO_EQU = "412";
	
	// 会员没有符合领取纸尿裤的条件
	public static final String PIPI_USER_GIFT_NO_FOUND = "413";
	
	// 您已经领过一次,不能再次领取
	public static final String PIPI_USER_HAS_ALREADY_GET = "414";
	
	// 该店没有POS机,不能领取
	public static final String PIPI_NO_POS= "415";
	
	// 指定的VIP医院才能参加此活动
	public static final String NO_HP_TERMINAL = "416";
	
	// 只能领取指定的活动礼品
	public static final String ONLY_CAN_GET_ACT_GIFT = "417";
	
	// 只能给指定的活动产品积分
	public static final String ONLY_CAN_REG_ACT_PRODUCT = "418";
	
	
	
	
	// =================== 系统异常应答码 ===================
	
	// 系统异常
	public static final String SYSTEM_EXCEPTION = "900";
	
	// 数据库异常
	public static final String DB_EXCEPTION = "901";

	// 密钥异常
	public static final String KEY_EXCEPTION = "902";

	// 数据异常
	public static final String DATA_EXCEPTION = "903";
	
	// 系统维护
	public static final String SYSTEM_MAINTAIN = "904";
	
	// 积分服务器系统异常
	public static final String EJB_EXCEPTION = "905";
	
	
	// http通信异常
	public static final String HTTP_ERROR = "5906";

	// http网络连接异常
	public static final String HTTP_SOKET_EXCEPTION = "5907";

	// http网络通信超时
	public static final String HTTP_SOKET_TIMEOUT = "5908"; 
	
	
	//绑定微博, 发现微博早已经被别的账号绑定过。
	public static final String WEIBO_IS_BOUNDED_BY_OTHER = "101"; 

}
