package com.chaboshi.scf.server.deploy.bytecode;

import com.chaboshi.scf.protocol.sdp.RequestProtocol;
import com.chaboshi.scf.protocol.utility.KeyValuePair;
import com.chaboshi.scf.server.contract.annotation.OperationContract;
import com.chaboshi.scf.server.contract.annotation.ServiceBehavior;
import com.chaboshi.scf.server.contract.context.IProxyFactory;
import com.chaboshi.scf.server.contract.context.IProxyStub;
import com.chaboshi.scf.server.contract.context.SCFContext;
import com.chaboshi.scf.server.contract.context.SCFResponse;
import com.chaboshi.scf.server.contract.entity.Out;
import com.chaboshi.scf.server.core.convert.ConvertFacotry;
import com.chaboshi.scf.server.core.convert.IConvert;
import com.chaboshi.scf.server.util.ErrorState;
import com.chaboshi.scf.server.util.ServiceFrameException;

/**
 * 
 * @author Service Platform Architecture Team (spat@58.com)
 * 
 *         <a href="http://blog.58.com/spat/">blog</a>
 *         <a href="http://www.58.com">website</a>
 * 
 */
public class Constant {

  /**
   * service contract config xml
   */
  public static final String SERVICE_CONTRACT = "serviceframe.xml";

  /**
   * out parameter name
   */
  public static final String OUT_PARAM = Out.class.getName();

  /**
   * IProxyStub class name
   */
  public static final String IPROXYSTUB_CLASS_NAME = IProxyStub.class.getName();

  /**
   * SCFContext class name
   */
  public static final String SCFCONTEXT_CLASS_NAME = SCFContext.class.getName();

  /**
   * SCFRequest class name
   */
  public static final String SCFRESPONSE_CLASS_NAME = SCFResponse.class.getName();

  /**
   * ServiceFrameException class name
   */
  public static final String SERVICEFRAMEEXCEPTION_CLASS_NAME = ServiceFrameException.class.getName();

  /**
   * Request protocol class name
   */
  public static final String REQUEST_PROTOCOL_CLASS_NAME = RequestProtocol.class.getName();

  /**
   * IConvert class name
   */
  public static final String ICONVERT_CLASS_NAME = IConvert.class.getName();

  /**
   * ConvertFactory class name
   */
  public static final String CONVERT_FACTORY_CLASS_NAME = ConvertFacotry.class.getName();

  /**
   * KeyValuePair protocol class name
   */
  public static final String KEYVALUEPAIR_PROTOCOL_CLASS_NAME = KeyValuePair.class.getName();

  /**
   * ErrorState class name
   */
  public static final String ERRORSTATE_CLASS_NAME = ErrorState.class.getName();

  /**
   * IProxyFactory class name
   */
  public static final String IPROXYFACTORY_CLASS_NAME = IProxyFactory.class.getName();

  /**
   * OperationContract class name
   */
  public static final String OPERATIONCONTRACT_CLASS_NAME = OperationContract.class.getName();

  /**
   * ServiceBehavior class name
   */
  public static final String SERVICEBEHAVIOR_CLASS_NAME = ServiceBehavior.class.getName();

  /**
   * ServiceContract class name
   */
  public static final String SERVICECONTRACT_CLASS_NAME = ContractInfo.class.getName();
}