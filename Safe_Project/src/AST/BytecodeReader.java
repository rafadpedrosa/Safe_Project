package AST;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.*;
import beaver.*;
import java.util.ArrayList;
import java.util.zip.*;
import java.io.*;

/**
 * @ast interface
 * @declaredat :0
 */
public interface BytecodeReader {

     
    CompilationUnit read(InputStream is, String fullName, Program p) throws FileNotFoundException, IOException;
}
