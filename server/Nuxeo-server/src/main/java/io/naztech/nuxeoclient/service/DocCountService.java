package io.naztech.nuxeoclient.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nazdaqTechnologies.core.service.AbstractService;
import com.nazdaqTechnologies.jdbc.JdbcResult;
import com.nazdaqTechnologies.jdbc.JdbcService;
import com.nazdaqTechnologies.jdbc.StoredProcedure.JdbcStoredProcedure;
import com.nazdaqTechnologies.jdbc.util.JdbcUtils;

import io.naztech.nuxeoclient.constants.ActionType;
import io.naztech.nuxeoclient.constants.SPName;
import io.naztech.nuxeoclient.model.DocCount;

public class DocCountService extends AbstractService<DocCount> {
	private static Logger log = LoggerFactory.getLogger(DocCountService.class);

	public List<DocCount> processAction(String action, DocCount docCount) {

		List<DocCount> templateList = new ArrayList<DocCount>();

		try {
			templateList = handleDocCount(action, docCount);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return templateList;
	}

	/**
	 * @author Asadullah.galib
	 * @param
	 * @desc:
	 */
	private List<DocCount> handleDocCount(String action, DocCount docCount) throws Exception {

		log.debug("executing...{}", action);

		List<DocCount> docCountList = new ArrayList<DocCount>();

		Map<String, Object> spArgsMap = JdbcService.createSqlMap(docCount, DocCount.getSql2BeanMap());

		JdbcResult jdbcResult = new JdbcResult();

		try {

			JdbcStoredProcedure jdbcStoredProcedure = getJdbcService()
					.getJdbcStoredProcedure(SPName.DOCUTECH.toString(), SPName.ACT_DOC_COUNT.toString());
			jdbcResult.setFilteredOutputParamMap(jdbcStoredProcedure.getSpOutputParamMap());
			jdbcResult.setProcessWarnings(false);

			spArgsMap.put("@tx_action_name", action);

			jdbcResult = getJdbcService().executeSP(action, SPName.DOCUTECH.toString(), SPName.ACT_DOC_COUNT.toString(),
					spArgsMap, jdbcResult);

			if (action.equalsIgnoreCase(ActionType.SELECT.toString())
					|| action.equalsIgnoreCase(ActionType.SEQUENCE.toString())) {
				docCountList = JdbcUtils.mapRows(DocCount.class, DocCount.getRs2BeanMap(),
						jdbcResult.getRsTypeMap(SPName.RS_TYPE_DOC_COUNT.toString()));
			} else {
				JdbcUtils.populateBean(docCount, DocCount.getSql2BeanMap(), jdbcResult.getOutputParamValueMap());

				docCountList.add(docCount);
			}

		} catch (Exception ex) {
			log.error("error {}, \nMessage *** : {}", ex, ex.getLocalizedMessage());
			throw ex;
		}

		return docCountList;
	}
}
