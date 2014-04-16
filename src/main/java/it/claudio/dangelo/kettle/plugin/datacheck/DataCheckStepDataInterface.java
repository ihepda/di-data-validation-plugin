package it.claudio.dangelo.kettle.plugin.datacheck;

import java.util.List;

import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

public class DataCheckStepDataInterface extends BaseStepData implements StepDataInterface{
	public List<CheckElement> elements;
}
