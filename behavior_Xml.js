// This is a coppercube behavior which moves the node it is attached to only on the x axis,
// controlled by the cursor keys and with space for 'jump'.
//
// The following embedded xml is for the editor and describes how the behavior can be edited:
// Supported types are: int, float, string, bool, color, vect3d, scenenode, texture, action
/*
	<behavior jsname="behavior_Xml" description="make an xml">
	<property name="Object" type="scenenode"/>
	</behavior>
*/

behavior_Xml = function()
{
};

/**
 * XMLWriter - XML generator for Javascript, based on .NET's XMLTextWriter.
 * Copyright (c) 2008 Ariel Flesler - aflesler(at)gmail(dot)com | http://flesler.blogspot.com
 * Licensed under BSD (http://www.opensource.org/licenses/bsd-license.php)
 * Date: 3/12/2008
 * @version 1.0.0
 * @author Ariel Flesler
 * http://flesler.blogspot.com/2008/03/xmlwriter-for-javascript.html
 */
 
function XMLWriter( encoding, version ){
	if( encoding )
		this.encoding = encoding;
	if( version )
		this.version = version;
};
(function(){
	
XMLWriter.prototype = {
	encoding:'ISO-8859-1',// what is the encoding
	version:'1.0', //what xml version to use
	formatting: 'indented', //how to format the output (indented/none)  ?
	indentChar:'\t', //char to use for indent
	indentation: 1, //how many indentChar to add per level
	newLine: '\n', //character to separate nodes when formatting
	//start a new document, cleanup if we are reusing
	writeStartDocument:function( standalone ){
		this.close();//cleanup
		this.stack = [ ];
		this.standalone = standalone;
	},
	//get back to the root
	writeEndDocument:function(){
		this.active = this.root;
		this.stack = [ ];
	},
	//set the text of the doctype
	writeDocType:function( dt ){
		this.doctype = dt;
	},
	//start a new node with this name, and an optional namespace
	writeStartElement:function( name, ns ){
		if( ns )//namespace
			name = ns + ':' + name;
		
		var node = { n:name, a:{ }, c: [ ] };//(n)ame, (a)ttributes, (c)hildren
		
		if( this.active ){
			this.active.c.push(node);
			this.stack.push(this.active);
		}else
			this.root = node;
		this.active = node;
	},
	//go up one node, if we are in the root, ignore it
	writeEndElement:function(){
		this.active = this.stack.pop() || this.root;
	},
	//add an attribute to the active node
	writeAttributeString:function( name, value ){
		if( this.active )
			this.active.a[name] = value;
	},
	//add a text node to the active node
	writeString:function( text ){
		if( this.active )
			this.active.c.push(text);
	},
	//shortcut, open an element, write the text and close
	writeElementString:function( name, text, ns ){
		this.writeStartElement( name, ns );
		this.writeString( text );
		this.writeEndElement();
	},
	//add a text node wrapped with CDATA
	writeCDATA:function( text ){
		this.writeString( '<![CDATA[' + text + ']]>' );
	},
	//add a text node wrapped in a comment
	writeComment:function( text ){
		this.writeString('<!-- ' + text + ' -->');
	},
	//generate the xml string, you can skip closing the last nodes
	flush:function(){		
		if( this.stack && this.stack[0] )//ensure it's closed
			this.writeEndDocument();
		
		var 
			chr = '', indent = '', num = this.indentation,
			formatting = this.formatting.toLowerCase() == 'indented',
			buffer = '<?xml version="'+this.version+'" encoding="'+this.encoding+'"';
			
		if( this.standalone !== undefined )
			buffer += ' standalone="'+!!this.standalone+'"';
		buffer += ' ?>';
		
		buffer = [buffer];
		
		if( this.doctype && this.root )
			buffer.push('<!DOCTYPE '+ this.root.n + ' ' + this.doctype+'>'); 
		
		if( formatting ){
			while( num-- )
				chr += this.indentChar;
		}
		
		if( this.root )//skip if no element was added
			format( this.root, indent, chr, buffer );
		
		return buffer.join( formatting ? this.newLine : '' );
	},
	//cleanup, don't use again without calling startDocument
	close:function(){
		if( this.root )
			clean( this.root );
		this.active = this.root = this.stack = null;
	},
	getDocument: window.ActiveXObject 
		? function(){ //MSIE
			var doc = new ActiveXObject('Microsoft.XMLDOM');
			doc.async = false;
			doc.loadXML(this.flush());
			return doc;
		}
		: function(){// Mozilla, Firefox, Opera, etc.
			return (new DOMParser()).parseFromString(this.flush(),'text/xml');
	}
};

//utility, you don't need it
function clean( node ){
	var l = node.c.length;
	while( l-- ){
		if( typeof node.c[l] == 'object' )
			clean( node.c[l] );
	}
	node.n = node.a = node.c = null;	
};

//utility, you don't need it
function format( node, indent, chr, buffer ){
	var 
		xml = indent + '<' + node.n,
		nc = node.c.length,
		attr, child, i = 0;
		
	for( attr in node.a )
		xml += ' ' + attr + '="' + node.a[attr] + '"';
	
	xml += nc ? '>' : ' />';

	buffer.push( xml );
		
	if( nc ){
		do{
			child = node.c[i++];
			if( typeof child == 'string' ){
				if( nc == 1 )//single text node
					return buffer.push( buffer.pop() + child + '</'+node.n+'>' );					
				else //regular text node
					buffer.push( indent+chr+child );
			}else if( typeof child == 'object' ) //element node
				format(child, indent+chr, chr, buffer);
		}while( i < nc );
		buffer.push( indent + '</'+node.n+'>' );
	}
};

})();

function isInList(x, xs)
{
	for(var iter=0; iter<xs.length;iter++)
	{
		if(xs[iter].localeCompare(x)==0)
		{
			return true;
		}
	}
	return false;
}

function createMesh(object,vw)
{
	var name = ccbGetSceneNodeProperty(object, "Name");
	if (name=="piano3"||name=="piano4"||name=="Mesh1"||name==""||name=="cubeMesh1") {
		return;
	}
	var position= ccbGetSceneNodeProperty(object, "Position");
	var rotation= ccbGetSceneNodeProperty(object, "Rotation");
	var scale= ccbGetSceneNodeProperty(object, "Scale");
	vw.writeStartElement("mesh");
	vw.writeElementString( "name" , name+".obj");
	vw.writeStartElement("data");
	vw.writeElementString( "file" , "data/meshes/CS4621/"+name+".obj");
	vw.writeEndElement();
	vw.writeEndElement();
	
	vw.writeStartElement("object");
	vw.writeElementString( "name" , name);
	vw.writeStartElement("data");
	vw.writeElementString( "mesh" , name+".obj");
	vw.writeElementString( "material" , "PhongMaterial");
	vw.writeElementString( "translation" , position.x+" "+position.y+" "+position.z);
	vw.writeElementString( "scale" , scale.x+" "+scale.y+" "+scale.z);
	vw.writeElementString( "rotation" , rotation.x+" "+rotation.y+" "+rotation.z);
	vw.writeEndElement();
	vw.writeEndElement();
}

// mouseEvent: 0=mouse moved, 1=mouse wheel moved, 2=left mouse up,  3=left mouse down, 4=right mouse up, 5=right mouse down
behavior_Xml.prototype.onMouseEvent = function(mouseEvent, mouseWheelDelta)
{
	if(mouseEvent==3)
	{
		var root = ccbGetRootSceneNode();
		var count = ccbGetSceneNodeChildCount(root);

		var vw = new XMLWriter( 'UTF-8', '1.0' );
		vw.writeStartDocument();
		vw.writeStartElement("scene");
		for(var i=0; i<count; ++i)
		{
			var child = ccbGetChildSceneNode(root, i);
			createMesh(child,vw);
		}
		vw.writeEndElement();
		vw.writeEndDocument();
		
		document.getElementById("xml").innerHTML=vw.flush();
		vw.close();
	}
}
