package com.easyfitness.machines;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.easyfitness.DAO.DAOFonte;
import com.easyfitness.DAO.DAOMachine;
import com.easyfitness.DAO.Machine;
import com.easyfitness.DAO.Profile;
import com.easyfitness.MainActivity;
import com.easyfitness.R;

import java.util.ArrayList;

public class MachineFragment extends Fragment {
	private String name;
	private int id;

	Spinner typeList = null;
	Spinner musclesList = null;
	EditText description = null;
	ImageButton renameMachineButton = null;
	ListView machineList = null;

	private DAOFonte mDbFonte = null;

	private DAOMachine mDbMachine = null;
	
	/**
	 * Create a new instance of DetailsFragment, initialized to
	 * show the text at 'index'.
	 */
	public static MachineFragment newInstance(String name, int id) {
		MachineFragment f = new MachineFragment();

		// Supply index input as an argument.
		Bundle args = new Bundle();
		args.putString("name", name);
		args.putInt("id", id);
		f.setArguments(args);

		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.tab_machine, container, false);

		//typeList = (Spinner) view.findViewById(R.id.filterDate);
		//machineList = (Spinner) view.findViewById(R.id.filterMachine);
        //renameMachineButton = (ImageButton) view.findViewById(R.id.imageMachineRename);
        machineList = (ListView) view.findViewById(R.id.listMachine);
		//musclesList = (Spinner) view.findViewById(R.id.listFilterRecord);
		
		machineList.setOnItemClickListener(onClickListItem);

		// Initialisation de l'historique
		mDbFonte = new DAOFonte(view.getContext());
		mDbMachine = new DAOMachine(view.getContext());

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();

		refreshData();
		
		// Initialisation des evenements
		machineList.setOnItemSelectedListener(onItemSelectedList);
	}

	private static String[] prepend(String[] a, String el) {
		String[] c = new String[a.length + 1];
		c[0] = el;
		System.arraycopy(a, 0, c, 1, a.length);
		return c;
	}

	public String getName() { 
		return getArguments().getString("name");
	}

	public int getFragmentId() { 
		return getArguments().getInt("id", 0);
	}
	
	private OnItemClickListener onClickListItem = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// Get Machine Name selected
			TextView textView = (TextView) view.findViewById(R.id.LIST_MACHINE_NAME);
			String machineName = textView.getText().toString();

			TextView textViewID = (TextView) view.findViewById(R.id.LIST_MACHINE_ID);
			long machineId = Long.valueOf(textViewID.getText().toString());

			MachineDetailsFragment machineDetailsFragment = MachineDetailsFragment.newInstance(machineId, ((MainActivity)getActivity()).getCurrentProfil().getId());
			FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
			// Replace whatever is in the fragment_container view with this fragment,
			// and add the transaction to the back stack so the user can navigate back
			transaction.replace(R.id.fragment_container, machineDetailsFragment, MainActivity.MACHINESDETAILS);
            transaction.addToBackStack(null);
			// Commit the transaction
			transaction.commit();
		}
	};

	private OnItemSelectedListener onItemSelectedList = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			//refreshData();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};


	public DAOFonte getDB() {
		return mDbFonte;
	}
	
	public MachineFragment getThis() {
		return this;
	}

	private void refreshData(){
        Cursor c = null;
		Cursor oldCursor = null;
        ArrayList<Machine> records = null;
		
		View fragmentView = getView();
		if(fragmentView != null) {
			if (getProfil() != null) {
			
				// Version avec table Machine
                c = mDbMachine.getAllMachines();
                if (c == null || c.getCount() == 0) {
					//Toast.makeText(getActivity(), "No records", Toast.LENGTH_SHORT).show();    
					machineList.setAdapter(null);
				} else {
					if ( machineList.getAdapter() == null ) {
                        MachineCursorAdapter mTableAdapter = new MachineCursorAdapter(this.getView().getContext(), c, 0);
						machineList.setAdapter(mTableAdapter);
					} else {				
						MachineCursorAdapter mTableAdapter = ((MachineCursorAdapter)machineList.getAdapter());
                        oldCursor = mTableAdapter.swapCursor(c);
						if (oldCursor!=null) oldCursor.close();
					}
				}
			}
		}
	}

	@Override
	public void onHiddenChanged (boolean hidden) {
		if (!hidden) refreshData();
	}

	private Profile getProfil()	{
		return ((MainActivity)getActivity()).getCurrentProfil();
	}

}
