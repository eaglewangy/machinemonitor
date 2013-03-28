<?php

//localhost:8080/mobile.php?action=users 
//localhost:8080/mobile.php?action=update&imei=865524010361957&owner=ying&version=4.0.4&manufacturer=google&status=available
$reqNumber = count($_GET);
if ($reqNumber > 0)
{
   if (isset($_GET["action"]))
   {
       $db = new SQLite3('mobile.sqlite');
	   //$db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
       $action = $_GET["action"];
	   if ($action == "users")
	   {
		  $result = $db->query('SELECT * FROM members');
		  $users ='';
	      while ($row = $result->fetchArray())
	      {
		     $users .= $row['owner']. ',';
	     }
		 $users  = substr_replace($users, "", -1);
		 echo($users);
	   }
	   else if ($action == "update")
	   {
	      $imei=$_GET['imei'];
		  $owner=$_GET['owner'];
		  $version=$_GET['version'];
		  $manufacturer=$_GET['manufacturer'];
		  $status=$_GET['status'];
	      $result = $db->query('SELECT count(*) FROM mobile where imei=' . $imei . '');
		  $rows = $result->fetchArray();
		  if ($rows[0] == 0)
		  {
			  $insert = "insert into mobile(imei,owner,version,manufacturer,status) values('" . $imei . "','" . $owner . "','" . $version . "','" . $manufacturer . "','" . $status . "')";
			  print $insert;
			  $result = $db->query($insert);
		  }
		  else{
			 $db->query("UPDATE mobile SET owner='" . $owner . "',version='" . $version . "', manufacturer='" . $manufacturer . "',status='" . $status . "' WHERE imei='" . $imei . "'");
		  }
	   }
	   
	   $db->close();
   }
}
else{

?>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Mobile Info</title>

<link href="media/css/demo_page.css" rel="stylesheet" type="text/css" />
<link href="media/css/demo_table.css" rel="stylesheet" type="text/css" />

<script type="text/javascript" language="javascript" src="media/js/jquery.js"></script>
<script class="jsbin" src="media/js/jquery.dataTables.min.js"></script>

<script type="text/javascript">
$(document).ready(function() {
    $('#machine_table').dataTable( {
       "aaSorting": [[1, 'desc'], [2, 'desc'],[3, 'desc'],[4, 'desc']]
    } );
} );
</script> 

</head>
<body>

<table cellpadding="0" cellspacing="0" border="0" class="display" id="machine_table">
<thead>
<tr>
	<th>IMEI</th>
	<th>Model</th>
   	<th>Version</th>
    <th>Owner</th>
    <th>Status</th>
</tr>
</thead>

<?php
 	try
	{
		//open the database
		$db = new PDO('sqlite:mobile.sqlite');
		$result = $db->query('SELECT * FROM mobile where imei is not null');
		foreach($result as $row)
		{
			echo("<tr class='even gradeC'><td>" . $row['imei'] . "</td>");
			echo("<td>" . $row['manufacturer'] . "</td>");
			echo("<td>" . $row['version'] . "</td>");
			echo("<td>" . $row['owner'] . "</td>");
			echo("<td>" . $row['status'] . "</td></tr>");
		}
		echo("</tbody>");
		echo("</table>");
		// close the database connection
		$db = NULL;
	}
	catch(PDOException $e)
	{
		echo('Exception : '. $e->getMessage());
	}

echo("</body>");
echo("</html>");

}
	
?>
