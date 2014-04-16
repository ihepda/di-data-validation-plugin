package it.claudio.dangelo.kettle.plugin.datacheck;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

public class DataCheckStepInterface extends BaseStep implements StepInterface{

	public DataCheckStepInterface(StepMeta stepMeta,
			StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi)
			throws KettleException {
		DataCheckMetaInterface metaInterface = (DataCheckMetaInterface) smi;
		DataCheckStepDataInterface dataInterface = (DataCheckStepDataInterface) sdi;
		
    	Object[] row = this.getRow();
    	if(row == null) {
    		this.setOutputDone();
    		return false;
    	}
    	incrementLinesInput();
    	RowMetaInterface inputRowMeta = this.getInputRowMeta();
    	if(dataInterface.elements != null || dataInterface.elements.size() > 0) {
    		
    	} else this.putRow(inputRowMeta, row);
		return true;
	}
	
}
