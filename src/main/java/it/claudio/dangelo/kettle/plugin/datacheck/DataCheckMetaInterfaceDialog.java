package it.claudio.dangelo.kettle.plugin.datacheck;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

public class DataCheckMetaInterfaceDialog extends BaseStepDialog implements
		StepDialogInterface {

	private static Class<?> PKG = DataCheckMetaInterface.class; // for i18n
	// purposes,
	// needed by
	// Translator2!!
	// $NON-NLS-1$

	private DataCheckMetaInterface metaInterface;
	private Composite sc;
	private ModifyListener lsMod;
	private TableView checks;
	private ColumnInfo[] ciKey = new ColumnInfo[3];
	private FormData fdCheck;

	public DataCheckMetaInterfaceDialog(Shell parent, Object baseStepMeta,
			TransMeta transMeta, String stepname) {
		super(parent, (DataCheckMetaInterface) baseStepMeta, transMeta,
				stepname);
		this.metaInterface = (DataCheckMetaInterface) baseStepMeta;
	}

	private Control addStepName(int middle, int margin) {
		// Stepname line
		wlStepname = new Label(sc, SWT.RIGHT);
		wlStepname.setText("Step Name:"); //$NON-NLS-1$
		props.setLook(wlStepname);
		fdlStepname = new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.right = new FormAttachment(middle, -margin);
		fdlStepname.top = new FormAttachment(0, margin);
		wlStepname.setLayoutData(fdlStepname);
		wStepname = new Text(sc, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
		props.setLook(wStepname);
		fdStepname = new FormData();
		fdStepname.left = new FormAttachment(middle, 0);
		fdStepname.top = new FormAttachment(0, margin);
		fdStepname.right = new FormAttachment(100, 0);
		wStepname.setLayoutData(fdStepname);
		return wStepname;
	}
	
	private Control addChecks(int middle, int margin, Control topControl) {
		int nrCols = 4;
		int nrRows = this.metaInterface.getCheckElements().size();
		if (nrRows == 0)
			nrRows = 1;
		ciKey = new ColumnInfo[nrCols];
		ciKey[0] = new ColumnInfo(BaseMessages.getString(PKG,
				"DataCheckDialog.ColumnInfo.Fieldname"),
				ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "" }, true);
		ciKey[1] = new ColumnInfo(BaseMessages.getString(PKG,
				"DataCheckDialog.ColumnInfo.Checker"),
				ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "" }, true);
		ciKey[2] = new ColumnInfo(BaseMessages.getString(PKG,
				"DataCheckDialog.ColumnInfo.Parameter"),
				ColumnInfo.COLUMN_TYPE_TEXT);
		ciKey[2] = new ColumnInfo(BaseMessages.getString(PKG,
				"DataCheckDialog.ColumnInfo.Message"),
				ColumnInfo.COLUMN_TYPE_TEXT);
		checks = new TableView(transMeta, sc, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL, ciKey, nrRows,
				lsMod, props);
		fdCheck = new FormData();
		fdCheck.left = new FormAttachment(0, 0);
		fdCheck.top = new FormAttachment(topControl,margin);
		fdCheck.right = new FormAttachment(100, 0);
		fdCheck.bottom = new FormAttachment(95);
		checks.setLayoutData(fdCheck);
		ciKey[0].setComboValues(getFields());
		return checks;
	}
	
	
	@Override
	public String open() {
		Shell parent = getParent();
		Display display = parent.getDisplay();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX
				| SWT.MIN);
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		shell.setText("Date dimension output"); //$NON-NLS-1$
		final ScrolledComposite scrollComposite = new ScrolledComposite(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		//sc = shell;
		//sc = new Composite(shell, SWT.V_SCROLL);
		sc = new Composite(scrollComposite, SWT.NONE);
		//props.setLook(sc);
		
		props.setLook(shell);
		setShellImage(shell, this.metaInterface);
		changed = this.metaInterface.hasChanged();

		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		sc.setLayout(formLayout);

		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		lsMod = new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				metaInterface.setChanged();
			}
		};
		
		Control topControl = this.addStepName(middle, margin);
		topControl = this.addChecks(middle, margin, topControl);
		topControl = this.addButtons(middle, margin, topControl);
		
		scrollComposite.setContent(sc);
		scrollComposite.setExpandVertical(true);
		scrollComposite.setExpandHorizontal(true);
		final Composite composite = sc;
		scrollComposite.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle r = scrollComposite.getClientArea();
				scrollComposite.setMinSize(composite.computeSize(r.width, SWT.DEFAULT));
			}
		});

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return stepname;

	}
	
	public String open_() {

		Shell parent = getParent();
		Display display = parent.getDisplay();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX
				| SWT.MIN);
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		shell.setText("Data validator");
		final ScrolledComposite scrollComposite = new ScrolledComposite(shell,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		// sc = shell;
		// sc = new Composite(shell, SWT.V_SCROLL);
		sc = new Composite(scrollComposite, SWT.NONE);

		lsMod = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				metaInterface.setChanged();
			}
		};
		
		props.setLook(shell);
		setShellImage(shell, this.metaInterface);
		changed = this.metaInterface.hasChanged();

		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		sc.setLayout(formLayout);

		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		// Stepname line
		wlStepname = new Label(sc, SWT.RIGHT);
		wlStepname.setText("Step Name:"); //$NON-NLS-1$
		props.setLook(wlStepname);
		fdlStepname = new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.right = new FormAttachment(middle, -margin);
		fdlStepname.top = new FormAttachment(0, margin);
		wlStepname.setLayoutData(fdlStepname);
		wStepname = new Text(sc, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
		wStepname.addModifyListener(lsMod);
		props.setLook(wStepname);
		fdStepname = new FormData();
		fdStepname.left = new FormAttachment(middle, 0);
		fdStepname.top = new FormAttachment(0, margin);
		fdStepname.right = new FormAttachment(100, 0);
		wStepname.setLayoutData(fdStepname);
		/*

		int nrCols = 3;
		int nrRows = this.metaInterface.getCheckElements().size();
		if (nrRows == 0)
			nrRows = 1;
		ciKey = new ColumnInfo[nrCols];
		ciKey[0] = new ColumnInfo(BaseMessages.getString(PKG,
				"DataCheckDialog.ColumnInfo.Fieldname"),
				ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "" }, true);
		ciKey[1] = new ColumnInfo(BaseMessages.getString(PKG,
				"DataCheckDialog.ColumnInfo.Checker"),
				ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "" }, true);
		ciKey[2] = new ColumnInfo(BaseMessages.getString(PKG,
				"DataCheckDialog.ColumnInfo.Parameter"),
				ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "" }, true);
		checks = new TableView(transMeta, sc, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL, ciKey, nrRows,
				lsMod, props);
		fdCheck = new FormData();
		fdCheck.left = new FormAttachment(0, 0);
		fdCheck.top = new FormAttachment(this.wlStepname, Const.MARGIN);
		fdCheck.right = new FormAttachment(100, 0);
		checks.setLayoutData(fdCheck);
		ciKey[0].setComboValues(getFields());
*/
		/*
		 * final Runnable runnable = new Runnable() { public void run() {
		 * StepMeta stepMeta = transMeta.findStep(stepname); if (stepMeta !=
		 * null) ciKey[0].setComboValues(getFields()); } }; new
		 * Thread(runnable).start();
		 */
//		this.addButtons(props.getMiddlePct(), Const.MARGIN, this.checks);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		return stepname;
	}

	private Control addButtons(int middle, int margin, Control topControl) {
		// THE BUTTONS
		wOK = new Button(sc, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "System.Button.OK")); //$NON-NLS-1$
		wCancel = new Button(sc, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel")); //$NON-NLS-1$

		BaseStepDialog.positionBottomButtons(sc, new Button[] { wOK, wCancel}, margin, topControl);

		lsOK = new Listener() {
			public void handleEvent(Event e) {
				ok();
			}
		};
		lsCancel = new Listener() {
			public void handleEvent(Event e) {
				cancel();
			}
		};

		wOK.addListener(SWT.Selection, lsOK);
		wCancel.addListener(SWT.Selection, lsCancel);
		return wOK;
	}

	private void cancel() {
		stepname = null;
		this.metaInterface.setChanged(backupChanged);
		dispose();
	}

	private void ok() {
		if (Const.isEmpty(wStepname.getText())) return;
		stepname = wStepname.getText();
		dispose();
	}

	private String[] getFields() {
		RowMetaInterface row;
		try {
			row = transMeta.getPrevStepFields(stepMeta);
		} catch (KettleStepException e) {
			throw new RuntimeException(e);
		}
		String[] results = new String[row.size()];
		for (int i = 0; i < row.size(); i++)
			results[i] = row.getValueMeta(i).getName();
		Arrays.sort(results);
		return results;
	}

}
