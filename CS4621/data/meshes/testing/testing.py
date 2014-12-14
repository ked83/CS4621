def parse(s):
    f = open(s+'.obj', 'r')
    f2 = open(s+'.mtl','r')
    count = 1
    jpglist=[]
    last_v = 0
    current_v = 0
    name=""
    for line in f2:
        if "melanie" in line:
            jpglist.append(line.split("/")[-1].replace("\n",""))
    for line in f:
        write=line
        if line[0]=="g":
            w.close()
            name = s+"__"+str(count)
            w=open(name+".obj","w")
            count=count+1
            last_v=last_v+current_v
            current_v=0
        elif line[0:2]=="v ":
            current_v=current_v+1
        elif line[0:6]=="usemtl":
            jpg = int(line.split("mat")[-1])
            xml.write("""
        <texture>
           <name>"""+name+"""</name>
           <data>
               <file>data/meshes/testing/"""+jpglist[jpg]+"""</file>
            </data>
        </texture>
        
        <mesh>
           <name>"""+name+""".obj</name>
           <data>
               <file>data/meshes/testing/"""+name+""".obj</file>
           </data>
       </mesh>
              
        <material>
           <name>"""+name+"""</name>
           <data>
               <type>Ambient</type>
               <diffuse>
                   <texture>"""+name+"""</texture>
               </diffuse>
           </data>
       </material>
       
       <object>
           <name>"""+name+"""</name>
           <data>
               <mesh>"""+name+""".obj</mesh>
               <material>"""+name+"""</material>
               """+trans+
               """
           </data>
       </object>
       """)
        elif line[0]=="f":
            write="f "
            counter=1
            for st in line[2:].split(" ")[0:3]:
                counter=counter+1
                for k in st.split("/"):
                    write=write+str(int(k)-last_v)+"/"
                write=write[:-1]+" "
            write=write+"\n"
        w.writelines(write)
    f.close()
    f2.close()
            

xmlReader = open("scene1.xml",'r')
name= ""
trans=""
record=False
w = open("t.txt",'w')
xml = open("t.xml","w")
for l in xmlReader:
    if "<object>" in l:
        record=True
    elif "</object>" in l:
        parse(name)
        record =False
        trans=""
    elif record:
        if "<name>" in l:
            left = l.index(">")
            l=l[left+1:]
            right = l.index("<")
            name=l[:right]
        elif ("<translation>" in l) or ("<scale>" in l) or ("<rotation>" in l):
            trans=trans+l
            
xml.close()