package it.claudio.dangelo.kettle.plugin.datacheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Node;

public class DataCheckMetaInterface extends BaseStepMeta implements StepMetaInterface {

	private List<CheckElement> checkElements;
	
	public List<CheckElement> getCheckElements() {
		return checkElements;
	}
	
	@Override
    public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev, String input[], String output[], RowMetaInterface info) {
		for (int i = 0; i < this.checkElements.size(); i++) {
			CheckElement checkElement = this.checkElements.get(i);
			if(checkElement.getField() == null || checkElement.getField().length() == 0)
				remarks.add(new CheckResult(CheckResult.TYPE_RESULT_ERROR,"Illegal argument, Field is mandatory for check " + i,stepMeta));
			if(checkElement.getChecker() == null )
				remarks.add(new CheckResult(CheckResult.TYPE_RESULT_ERROR,"Illegal argument, Checker is mandatory for field " + i,stepMeta));
		}
	}

	@Override
	public StepInterface getStep(StepMeta arg0, StepDataInterface arg1,
			int arg2, TransMeta arg3, Trans arg4) {
		return new DataCheckStepInterface(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public StepDataInterface getStepData() {
		return new DataCheckStepDataInterface();
	}

	@Override
	public String getXML() throws KettleException {
        StringBuffer retval = new StringBuffer();
        for (CheckElement checkElement : this.checkElements) {
			retval.append("<check ");
			retval.append("field=\"").append(checkElement.getField()).append('\"');
			retval.append(" checker=\"").append(checkElement.getChecker().getClass().getName()).append('\"');
			if(checkElement.getParameter() != null && checkElement.getParameter().length() > 0)
				retval.append(" parameter=\"").append(checkElement.getParameter()).append('\"');
			retval.append(" />");
		}
		return retval.toString();
	}
	
	@Override
	public void loadXML(Node arg0, List<DatabaseMeta> arg1,
			Map<String, Counter> arg2) throws KettleXMLException {
		List<Node> nodes = XMLHandler.getNodes(arg0, "check");
		this.checkElements = new ArrayList<CheckElement>();
		for (Node node : nodes) {
			String field = XMLHandler.getTagAttribute(node, "field");
			String checker = XMLHandler.getTagAttribute(node, "checker");
			String parameter = XMLHandler.getTagAttribute(node, "parameter");
//				Object checkerObject = Class.forName(checker).newInstance();
			this.checkElements.add(new CheckElement(field, checker, parameter));
		}
		
	}

	@Override
	public void readRep(Repository rep, ObjectId id_step, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleException {
		int checks = rep.countNrStepAttributes(id_step, "field");
		this.checkElements = new ArrayList<CheckElement>();
		for (int i=0;i<checks;i++) {
			String field   = rep.getStepAttributeString(id_step, i, "field"); //$NON-NLS-1$
			String checker = rep.getStepAttributeString(id_step, i, "checker");
			String parameter    = rep.getStepAttributeString(id_step, i, "parameter"); //$NON-NLS-1$
//				Object checkerObject = Class.forName(checker).newInstance();
			this.checkElements.add(new CheckElement(field, checker, parameter));
		}

	}

	@Override
	public void saveRep(Repository rep, ObjectId id_transformation, ObjectId id_step)
			throws KettleException {
		for (int i = 0; i < this.checkElements.size(); i++) {
			CheckElement checkElement = this.checkElements.get(i);
			rep.saveStepAttribute(id_transformation, id_step, i, "field", checkElement.getField());
			rep.saveStepAttribute(id_transformation, id_step, i, "checker", checkElement.getChecker().getClass().getName());
			if(checkElement.getParameter() != null && checkElement.getParameter().length() > 0)
				rep.saveStepAttribute(id_transformation, id_step, i, "parameter", checkElement.getParameter());
		}
		
	}

	@Override
	public void setDefault() {
		this.checkElements = new ArrayList<CheckElement>();
		
	}
	
}