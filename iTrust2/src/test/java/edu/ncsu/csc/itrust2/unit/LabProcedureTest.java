package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.admin.LabProcedureCodeForm;
import edu.ncsu.csc.itrust2.models.persistent.LabProcedureCode;

/**
 * Class that strives to get coverage of labprocedurecode and labprocedure
 * classes
 * 
 * @author Nick Sherrill
 *
 */
public class LabProcedureTest {
    /**
     * test all of the major functionality of lab procedure codes and lab
     * procedures
     * 
     * @throws Exception
     */
    @Test
    public void testLabProcedures () throws Exception {
        // let's try to stress test creating a new lab procedure code
        LabProcedureCodeForm form = new LabProcedureCodeForm();
        LabProcedureCode code = new LabProcedureCode();
        Long l = code.getId();

        // invalid description
        form.setCode( "12345-6" );
        String s = randomStringOfInts( 251 );
        form.setId( l );
        form.setDescription( s );
        try {
            code = new LabProcedureCode( form );
            fail( "the description should be too long" );
        }
        catch ( IllegalArgumentException e ) {
            assertEquals( "Description too long (250 characters max): " + s, e.getMessage() );
        }

        // the code is too short
        s = randomStringOfInts( 4 );
        form.setCode( s );
        // set the description back to valid
        // I can still use a string of ints as a description
        String d = randomStringOfInts( 20 );
        form.setDescription( d );
        try {
            code = new LabProcedureCode( form );
            fail( "the code is too short" );
        }
        catch ( IllegalArgumentException e ) {
            assertEquals( "Code must be seven characters: " + s, e.getMessage() );
        }

        // code doesn't have a hyphen at array index 5
        s = randomStringOfInts( 7 );
        form.setCode( s );
        try {
            code = new LabProcedureCode( form );
            fail( "code doesn't have a dash" );
        }
        catch ( IllegalArgumentException e ) {
            assertEquals( "Sixth character of code must be a hyphen: " + s, e.getMessage() );
        }

        // code has a character in it at stategic points (that get us more
        // coverage)
        s = randomCode( 0 );
        form.setCode( s );
        try {
            code = new LabProcedureCode( form );
            fail( "invalid character at idx 0" );
        }
        catch ( IllegalArgumentException e ) {
            assertEquals( "First five characters must be digits: " + s, e.getMessage() );
        }
        s = randomCode( 4 );
        form.setCode( s );
        try {
            code = new LabProcedureCode( form );
            fail( "invalid character at idx 4" );
        }
        catch ( IllegalArgumentException e ) {
            assertEquals( "First five characters must be digits: " + s, e.getMessage() );
        }
        s = randomCode( 6 );
        form.setCode( s );
        try {
            code = new LabProcedureCode( form );
            fail( "invalid character at idx 6" );
        }
        catch ( IllegalArgumentException e ) {
            assertEquals( "Seventh character of code must be a digit: " + s, e.getMessage() );
        }

        // now let's do something valid
        s = randomCode( -1 );
        form.setCode( s );
        try {
            code = new LabProcedureCode( form );
            assertEquals( l, code.getId() );
            assertEquals( s, code.getCode() );
            assertEquals( d, code.getDescription() );
        }
        catch ( IllegalArgumentException e ) {
            fail( "THIS TEST WAS SUPPOSED TO BE VALID AND IT ALL WENT WRONG!" );
        }

        /**
         * Now that we have a valid code, let's try to set up a procedure in
         * every way it could go wrong
         */
    }

    /**
     * creates a random LOINC code. If idx is between 0 and 4 or idx is 6, then
     * the method will create an invalid code.
     * 
     * @param idx
     *            the index of the character that you want to be invalid
     * @return the randomly generated LOINC code
     */
    private String randomCode ( int idx ) {
        String s = "";
        Random r = new Random();
        // write the first 5 numbers
        for ( int i = 0; i < 5; i++ ) {
            if ( i == idx ) {
                s = s + 'a';
            }
            else {
                s = s + (char) ( r.nextInt( 10 ) + '0' );
            }
        }
        // write the hyphen
        s = s + "-";
        // write the last number
        if ( idx == 6 ) {
            s = s + 'a';
        }
        else {
            s = s + (char) ( r.nextInt( 10 ) + '0' );
        }
        System.out.println( "generated code: " + s );
        return s;
    }

    /**
     * just creates a random string of integers of a specified length
     * 
     * @param len
     *            the length of the string
     * @return a randomly generated string of integers
     */
    private String randomStringOfInts ( int len ) {
        String s = "";
        Random r = new Random();
        for ( int i = 0; i < len; i++ ) {
            s = s + (char) ( r.nextInt( 10 ) + '0' );
        }
        System.out.println( "generated string: " + s );
        return s;
    }
}
