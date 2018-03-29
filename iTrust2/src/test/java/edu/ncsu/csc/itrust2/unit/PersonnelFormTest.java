package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.personnel.PersonnelForm;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.persistent.ER;
import edu.ncsu.csc.itrust2.models.persistent.LabTech;
import edu.ncsu.csc.itrust2.models.persistent.Personnel;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Tests for the PersonnelForm class
 *
 * @author jshore
 *
 */
public class PersonnelFormTest {

    /**
     * Test the PersonnelForm class.
     */
    @Test
    public void testPersonnelForm () {
        Personnel person = new Personnel();
        person.setSelf( new User( "username", "password", Role.ROLE_PATIENT, 1 ) );
        person.setFirstName( "first" );
        person.setLastName( "last" );
        person.setAddress1( "address1" );
        person.setAddress2( "address2" );
        person.setCity( "city" );
        person.setState( State.NC );
        person.setZip( "27606" );
        person.setPhone( "111-111-1111" );
        person.setSpecialty( "special" );
        person.setEmail( "email@email.com" );
        person.setId( 1L );
        final PersonnelForm form = new PersonnelForm( person );
        assertEquals( "username", form.getSelf() );
        assertEquals( "first", form.getFirstName() );
        assertEquals( "last", form.getLastName() );
        assertEquals( "address1", form.getAddress1() );
        assertEquals( "address2", form.getAddress2() );
        assertEquals( "city", form.getCity() );
        assertEquals( State.NC.getAbbrev(), form.getState() );
        assertEquals( "27606", form.getZip() );
        assertEquals( "111-111-1111", form.getPhone() );
        assertEquals( "special", form.getSpecialty() );
        assertEquals( "email@email.com", form.getEmail() );
        assertEquals( "1", form.getId() );

        person = new Personnel();
        person.setSelf( new User( "emt", "password", Role.ROLE_ER, 1 ) );
        person.setFirstName( "emergency" );
        person.setLastName( "responder" );
        person.setAddress1( "addr1" );
        person.setAddress2( "addr2" );
        person.setCity( "anywhere" );
        person.setState( State.NC );
        person.setZip( "28803" );
        person.setPhone( "222-222-2222" );
        person.setSpecialty( "specialty1" );
        person.setEmail( "coolemail@email.com" );
        person.setId( 2L );
        final PersonnelForm erForm = new PersonnelForm( person );
        final ER e = new ER( erForm );

        person.setSelf( new User( "technician", "password", Role.ROLE_LABTECH, 1 ) );
        person.setFirstName( "tech" );
        person.setLastName( "nician" );
        person.setAddress1( "addr3" );
        person.setAddress2( "addr4" );
        person.setCity( "everywhere" );
        person.setState( State.SC );
        person.setZip( "28806" );
        person.setPhone( "322-222-2222" );
        person.setSpecialty( "specialty2" );
        person.setEmail( "goodemail@email.com" );
        person.setId( 3L );
        final PersonnelForm ltForm = new PersonnelForm( person );
        final LabTech lt = new LabTech( ltForm );

        person.setSelf( new User( "healthcaredude", "password", Role.ROLE_HCP, 1 ) );
        person.setFirstName( "health" );
        person.setLastName( "care" );
        person.setAddress1( "addr5" );
        person.setAddress2( "addr6" );
        person.setCity( "nowhere" );
        person.setState( State.VA );
        person.setZip( "28774" );
        person.setPhone( "422-222-2222" );
        person.setSpecialty( "specialty3" );
        person.setEmail( "greatemail@email.com" );
        person.setId( 4L );
        final PersonnelForm hcpForm = new PersonnelForm( person );
        final Personnel h = new Personnel( hcpForm );

        assertEquals( "addr1", e.getAddress1() );
        assertEquals( "addr2", e.getAddress2() );
        assertEquals( "anywhere", e.getCity() );
        assertEquals( "coolemail@email.com", e.getEmail() );
        assertEquals( "emergency", e.getFirstName() );
        assertEquals( "2", "" + e.getId() );
        assertEquals( "responder", e.getLastName() );
        assertEquals( "222-222-2222", e.getPhone() );
        assertEquals( "specialty1", e.getSpecialty() );
        assertEquals( State.NC.getAbbrev(), "" + e.getState() );
        assertEquals( "28803", e.getZip() );

        assertEquals( "addr3", lt.getAddress1() );
        assertEquals( "addr4", lt.getAddress2() );
        assertEquals( "everywhere", lt.getCity() );
        assertEquals( "goodemail@email.com", lt.getEmail() );
        assertEquals( "tech", lt.getFirstName() );
        assertEquals( "3", "" + lt.getId() );
        assertEquals( "nician", lt.getLastName() );
        assertEquals( "322-222-2222", lt.getPhone() );
        assertEquals( "specialty2", lt.getSpecialty() );
        assertEquals( State.SC.getAbbrev(), "" + lt.getState() );
        assertEquals( "28806", lt.getZip() );

        assertEquals( "addr5", h.getAddress1() );
        assertEquals( "addr6", h.getAddress2() );
        assertEquals( "nowhere", h.getCity() );
        assertEquals( "greatemail@email.com", h.getEmail() );
        assertEquals( "health", h.getFirstName() );
        assertEquals( "4", "" + h.getId() );
        assertEquals( "care", h.getLastName() );
        assertEquals( "422-222-2222", h.getPhone() );
        assertEquals( "specialty3", h.getSpecialty() );
        assertEquals( State.VA.getAbbrev(), "" + h.getState() );
        assertEquals( "28774", h.getZip() );
    }
}
