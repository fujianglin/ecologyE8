package weaver.workflow.request;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.cache.CacheBase;
import weaver.cache.CacheColumn;
import weaver.cache.CacheColumnType;
import weaver.cache.CacheItem;
import weaver.cache.CacheMap;
import weaver.cache.PKColumn;
import weaver.conn.RecordSet;
import weaver.general.Util;

public class BrowserFieldValueComInfo extends CacheBase {
	private static final Log log = LogFactory
			.getLog(BrowserFieldValueComInfo.class);

	@PKColumn(type = CacheColumnType.STRING)
	protected static String PK_NAME = "id";

	@CacheColumn
	protected static int fieldvalue;

	@CacheColumn
	protected static int fieldvalueshowname;

	public CacheMap initCache() throws Exception {
		CacheMap localCacheMap = super.createCacheMap();
		RecordSet localRecordSet = new RecordSet();
		localRecordSet.executeQuery(getQuerySql(localRecordSet.getDBType(),
				null), new Object[0]);
		while (localRecordSet.next()) {
			String str = Util.null2String(localRecordSet.getString(PK_NAME));
			CacheItem localCacheItem = createCacheItem();
			parseResultSetToCacheItem(localRecordSet, localCacheItem);
			localCacheMap.put(str, localCacheItem);
		}
		return localCacheMap;
	}

	protected CacheItem initCache(String paramString) {
		if ((paramString == null) || (paramString.trim().length() == 0)) {
			return null;
		}
		String[] arrayOfString = Util.splitString(paramString, "_");
		if (arrayOfString.length != 3)
			return null;
		RecordSet localRecordSet = new RecordSet();
		localRecordSet.executeQuery(getQuerySql(localRecordSet.getDBType(),
				arrayOfString), new Object[0]);
		if (localRecordSet.next()) {
			CacheItem localCacheItem = createCacheItem();
			parseResultSetToCacheItem(localRecordSet, localCacheItem);
			return localCacheItem;
		}
		return null;
	}

	private static String getQuerySql(String paramString,
			String[] paramArrayOfString) {
		String str = "";
		if ("oracle".equals(paramString))
			str = "select requestid||'_'||fieldid||'_'||detailid as id,fieldvalue,fieldvalueshowname from workflow_fielddata_cache ";
		else if ("sqlserver".equals(paramString))
//			str = "select requestid+'_'+fieldid+'_'+detailid as id,fieldvalue,fieldvalueshowname from workflow_fielddata_cache ";
			str = "select CONVERT(varchar(50),requestid)+'_'+fieldid+'_'+CONVERT(varchar(50),detailid) as id,fieldvalue,fieldvalueshowname from workflow_fielddata_cache ";
		else if ("mysql".equals(paramString)) {
			str = "select concat_ws('_',requestid,fieldid,detailid) as id,fieldvalue,fieldvalueshowname from workflow_fielddata_cache";
		}
		if (paramArrayOfString != null) {
			str = str + " where requestid = " + paramArrayOfString[0]
					+ " and fieldid = '" + paramArrayOfString[1]
					+ "' and detailid = " + paramArrayOfString[2];
		}
		return str;
	}

	public String getId() {
		return (String) getRowValue(0);
	}

	public String getFieldValue() {
		return (String) getRowValue(fieldvalue);
	}

	public String getFieldValue(String paramString) {
		return (String) getValue(fieldvalue, paramString);
	}

	public String getFieldValueShowName() {
		return (String) getRowValue(fieldvalueshowname);
	}

	public String getFieldValueShowName(String paramString) {
		return (String) getValue(fieldvalueshowname, paramString);
	}

	public int getBrowserNum() {
		return size();
	}

	@Deprecated
	public boolean next(String paramString) {
		return false;
	}

	public void setToFirstrow() {
		setTofirstRow();
	}
}