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

<script type="text/javascript" src="js/jquery-latest.js"></script> 
<script type="text/javascript" src="js/jquery.tablesorter.min.js"></script> 

<script type="text/javascript">
$(document).ready(function() 
    { 
        $("#myTable").tablesorter(); 
    } 
); 
</script> 

</head>
<body>

<table id="myTable" class="tablesorter"> 
<thead> 
<tr> 
    <th>Last Name</th> 
    <th>First Name</th> 
    <th>Email</th> 
    <th>Due</th> 
    <th>Web Site</th> 
</tr> 
</thead> 
<tbody> 
<tr> 
    <td>Smith</td> 
    <td>John</td> 
    <td>jsmith@gmail.com</td> 
    <td>$50.00</td> 
    <td>http://www.jsmith.com</td> 
</tr> 
<tr> 
    <td>Bach</td> 
    <td>Frank</td> 
    <td>fbach@yahoo.com</td> 
    <td>$50.00</td> 
    <td>http://www.frank.com</td> 
</tr> 
<tr> 
    <td>Doe</td> 
    <td>Jason</td> 
    <td>jdoe@hotmail.com</td> 
    <td>$100.00</td> 
    <td>http://www.jdoe.com</td> 
</tr> 
<tr> 
    <td>Conway</td> 
    <td>Tim</td> 
    <td>tconway@earthlink.net</td> 
    <td>$50.00</td> 
    <td>http://www.timconway.com</td> 
</tr> 
</tbody> 
</table> 

<?php
 	try
	{
		//open the database
		$db = new PDO('sqlite:mobile.sqlite');
		print "<table border=1>";
		print "<tr><td>owner</td><td>version</td><td>manufacturer</td><td>status</td></tr>";
		$result = $db->query('SELECT * FROM mobile');
		foreach($result as $row)
		{
			print "<tr><td>".$row['owner']."</td>";
			print "<td>".$row['version']."</td>";
			print "<td>".$row['manufacturer']."</td>";
			print "<td>".$row['status']."</td></tr>";
		}
		print "</table>";
		// close the database connection
		$db = NULL;
	}
	catch(PDOException $e)
	{
		print 'Exception : '. $e->getMessage();
	}

echo("</body>");
echo("</html>");

}
	
?>
