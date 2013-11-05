package com.ruyicai.cas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.jasig.services.persondir.IPersonAttributes;
import org.jasig.services.persondir.support.CaseInsensitiveAttributeNamedPersonImpl;
import org.jasig.services.persondir.support.CaseInsensitiveNamedPersonImpl;
import org.jasig.services.persondir.support.MultivaluedPersonAttributeUtils;
import org.jasig.services.persondir.support.jdbc.AbstractJdbcPersonAttributeDao;
import org.jasig.services.persondir.support.jdbc.ColumnMapParameterizedRowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public class MySingleRowJdbcPersonAttributeDao extends AbstractJdbcPersonAttributeDao<Map<String, Object>>  {
	private static final ParameterizedRowMapper<Map<String, Object>> MAPPER = new ColumnMapParameterizedRowMapper(true);

    public MySingleRowJdbcPersonAttributeDao(DataSource ds, String sql) {
        super(ds, sql);
    }
    @Override
    protected ParameterizedRowMapper<Map<String, Object>> getRowMapper() {
        return MAPPER;
    }

    @Override
    protected List<IPersonAttributes> parseAttributeMapFromResults(List<Map<String, Object>> queryResults, String queryUserName) {
        final List<IPersonAttributes> peopleAttributes = new ArrayList<IPersonAttributes>(queryResults.size());
        
        for (final Map<String, Object> queryResult : queryResults) {
            final Map<String, List<Object>> multivaluedQueryResult = MultivaluedPersonAttributeUtils.toMultivaluedMap(queryResult);
            
            Set<String> keySet = queryResult.keySet();
            for(String key : keySet) {
            	System.out.println(key + ":" + queryResult.get(key));
            }
            final IPersonAttributes person;
            if (queryUserName != null) {
                person = new CaseInsensitiveNamedPersonImpl(queryUserName, multivaluedQueryResult);
            }
            else {
                final String userNameAttribute = this.getConfiguredUserNameAttribute();
                person = new CaseInsensitiveAttributeNamedPersonImpl(userNameAttribute, multivaluedQueryResult);
            }
            
            peopleAttributes.add(person);
        }
        
        return peopleAttributes;
    }
}
