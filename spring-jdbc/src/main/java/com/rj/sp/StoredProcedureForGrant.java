package com.rj.sp;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class StoredProcedureForGrant {

	@Autowired
	private JdbcTemplate template;
	
	@PostConstruct
    public void init() {
        // o_name and O_NAME, same
		template.setResultsMapCaseInsensitive(true);
    }
	
	public void executeProcedure(String username) {
		System.out.println("Method executed....."+username);
    	String SQL_GRANT_PROCEDURE = " CREATE OR REPLACE PROCEDURE grantAccess "
    			+ "begin"
    			+ " for tab1 in (select table_name from dba_tables where owner='"+username+"')"
    					+ " LOOP"
    					+ " execute immediate 'grant select on "+username+".'||tab1.table_name||' to test';"
    							+ " end loop; "
    							+ " end;"
    							+ "/";
    	
    	template.execute(SQL_GRANT_PROCEDURE);
    }
	
	public void executeProcedure_1(String user) {
		String procedure = "begin\n" + 
				"for tab1 in (select table_name from dba_tables where owner='"+user+"')\n" + 
				"LOOP\n" + 
				"execute immediate 'grant select on "+user+".'||tab1.table_name||' to test';\n" + 
				"end loop;\n" + 
				"end;";
		template.execute(procedure);
	}
}
