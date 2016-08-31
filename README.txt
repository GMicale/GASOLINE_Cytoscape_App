Readme file for GASOLINE application .exe file for Windows: version 2.0
Author: Giovanni Micale
Released: 06/02/2015

This .txt file contains the instructions to run the GASOLINE .exe file for Windows

---COMPILING INSTRUCTIONS

From the directory with java source code type:
javac -cp . *.class

---INPUT FILES---

a) INPUT NETWORKS FOLDER (Mandatory):

To run GASOLINE you need at least to specify the folder where input networks are located. The input folder MUST contain ONLY the networks to align.
The orthology .txt file must be placed in ANOTHER folder.
Input networks are provided as text files, where each row defines an edge with the following format:

Source_interactor1	Dest_interactor1	Weight1
Source_interactor2	Dest_interactor2	Weight2
...

where "Source_interactor" and "Dest_interactor" are protein ids, and weight is a float number between 0 and 1.
Fields in a row are separated by a tab character.
Example:

YDR012W	YDR471W	0.973
YDR012W	YDR489W	0.672
YDR473S		YDR254G		0.687
...

b) ORTHOLOGY SIMILARITY FILE (Optional):

Orthology similarity file defines the similarity scores between proteins of different networks.
Each row of the orthology file reports the BLAST E-values scores between two proteins of different networks, 
according to the following format:

proteinX_species1	proteinY_species2	Blast_evalue1
proteinX_species1	proteinZ_species2	Blast_evalue2
...

In each row, fields are separated by a tab character.
Example:

4R79.1a	FBpp0078297	 4e0-36
4R79.1a	FBpp0080312	 1e0-25
4R79.1a	FBpp0080340	 2e0-29
...

If no orthology file is provided, protein names are used to evaluate similarities. 
This means that two proteins of different networks are considered orthologs if they have the same name.


---OUTPUT FILES---

When GASOLINE finishes, all alignments found are saved in .txt files, one for each alignment found, and placed in
the output folder the user has previously specified (if any). 
In each output file the first three rows contain the total score of the alignment (complexSize*ISC score),
complex size (number of matched proteins) and ISC score (overall structural similarity).
Then there is the descriptions of matched subgraphs in terms on nodes and edges and the final mapping, 
where each row contains the matched proteins (one for each species) in the local alignment.
If no output folder is provided, results will be just printed on screen and not saved on disk.


---USAGE OF GASOLINE---

java -cp . GASOLINE -i <networkFolder> [-sim <orthoSimFile> -sig <sigma> -ds <densityThresh> -ov <overlapThresh> -it <iterIterative> -ms <minAlignSize> -o <outputFolder>]

Parameters within square brackets are optional.


---DESCRIPTION OF PARAMETERS---

-i: the path of the folder containing input networks files;
-sim: the orthology similarity file;
-o: the path of the output folder, where the final local alignments will be saved;
-sig: minimum network degree of initially aligned nodes (must be an integer, e.g. 3);
-ds: minimum average density of aligned complexes (must be a float between 0 and 1, e.g. 0.8);
-ov: maximum allowed percentage of overlap between two local alignments (must be a float between 0 and 1, e.g. 0.5);
-it: number of iterations of the iterative phase (must be an integer, e.g. 20);
-ms: minimum size of complexes in the final set of local alignments found (must be an integer, e.g. 4);

Default values for parameters:
-sig: 3;
-ds: 0.8
-ov: 0.5;
-it: 20;
-ms: 4;
