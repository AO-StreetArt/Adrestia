// Column Definitions for the Scene Table
var scnColumnDefs = [
  {
    headerName: "ID",
    field: "key",
    pinned: 'left',
    filter: "agTextColumnFilter",
    filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition:true, caseSensitive:true },
    editable: false
  },
  {
    headerName: "Name",
    field: "name",
    pinned: 'left',
    filter: "agTextColumnFilter",
    filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition:true, caseSensitive:true },
    editable: false
  },
  {
    headerName: "Region",
    field: "region",
    filter: "agTextColumnFilter",
    filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition:true, caseSensitive:true },
    editable: false
  },
  {
    headerName: "Latitude",
    field: "latitude",
    filter: "agTextColumnFilter",
    filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition:true, caseSensitive:true },
    editable: false
  },
  {
    headerName: "Longitude",
    field: "longitude",
    filter: "agTextColumnFilter",
    filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition:true, caseSensitive:true },
    editable: false
  }
];

// Column Definitions for the Object Table
var objColumnDefs = [
  {
    headerName: "ID",
    field: "key",
    pinned: 'left',
    filter: "agTextColumnFilter",
    filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition:true, caseSensitive:true },
    editable: false
  },
  {
    headerName: "Name",
    field: "name",
    pinned: 'left',
    filter: "agTextColumnFilter",
    filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition:true, caseSensitive:true },
    editable: false
  },
  {
    headerName: "Parent",
    field: "parent",
    filter: "agTextColumnFilter",
    filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition:true, caseSensitive:true },
    editable: false
  },
  {
    headerName: "Type",
    field: "type",
    filter: "agTextColumnFilter",
    filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition:true, caseSensitive:true },
    editable: false
  },
  {
    headerName: "Subtype",
    field: "subtype",
    filter: "agTextColumnFilter",
    filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition:true, caseSensitive:true },
    editable: false
  },
  {
    headerName: "Owner",
    field: "owner",
    filter: "agTextColumnFilter",
    filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition:true, caseSensitive:true },
    editable: false
  },
  {
    headerName: "Frame",
    field: "frame",
    filter: "agTextColumnFilter",
    filterParams: { applyButton: true, clearButton:true, filterOptions:["equals"], suppressAndOrCondition:true, caseSensitive:true },
    editable: false
  }
];
