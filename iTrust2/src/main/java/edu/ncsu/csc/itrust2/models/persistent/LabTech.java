package edu.ncsu.csc.itrust2.models.persistent;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.criterion.Criterion;
import org.hibernate.validator.constraints.Length;

import edu.ncsu.csc.itrust2.forms.personnel.PersonnelForm;
import edu.ncsu.csc.itrust2.models.enums.State;

/**
 * Database-persisted representation of all non-Patient types of iTrust2 users.
 * This includes HCPs, Admins, and other future users.
 *
 * @author Kai Presler-Marshall
 *
 */
@Entity
@Table ( name = "LabTech" )
public class LabTech extends DomainObject<Personnel> {

    /**
     * Get the LabTech by username
     *
     * @param username
     *            the username of the LabTech to get
     * @return the LabTech result with the queried username
     */
    public static LabTech getByName ( final String username ) {
        return getByName( User.getByName( username ) );
    }

    /**
     * Get the LabTech by username, done by passing the User representation of
     * the LabTech
     *
     * @param self
     *            the self the user representation of the LabTech with their
     *            username
     * @return the LabTech result with the queried username
     */
    public static LabTech getByName ( final User self ) {
        try {
            return getWhere( createCriterionAsList( "self", self ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Get all LabTech in the DB
     *
     * @SuppressWarnings for Unchecked cast from List<capture#1-of ? extends
     *                   DomainObject> to List<LabTech> Because get all just
     *                   returns a list of DomainObjects, the cast is okay.
     *
     * @return all LabTech in the DB
     */
    @SuppressWarnings ( "unchecked" )
    static public List<LabTech> getLabTech () {
        return (List<LabTech>) getAll( LabTech.class );
    }

    /**
     * Get all LabTech in the database where the passed query is true
     *
     * @SuppressWarnings for Unchecked cast from List<capture#1-of ? extends
     *                   DomainObject> to List<LabTech> Because get all just
     *                   returns a list of DomainObjects, the cast is okay.
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return all LabTech in the database where the passed query is true
     */
    @SuppressWarnings ( "unchecked" )
    private static List<LabTech> getWhere ( final List<Criterion> where ) {
        return (List<LabTech>) getWhere( LabTech.class, where );
    }

    /**
     * This stores a reference to the User object that this LabTech is.
     * Mandatory.
     */
    @JoinColumn ( name = "self_id", columnDefinition = "varchar(100)" )
    @OneToOne
    private User    self;

    /**
     * Whether or not the LabTech is enabled
     */
    private Integer enabled;

    /**
     * The first name of the LabTech
     */
    @Length ( max = 20 )
    private String  firstName;

    /**
     * The last name of the LabTech
     */
    @Length ( max = 30 )
    private String  lastName;

    /**
     * The address line 1 of the LabTech
     */
    @Length ( max = 50 )
    private String  address1;

    /**
     * The address line 2 of the LabTech
     */
    @Length ( max = 50 )
    private String  address2;

    /**
     * The city of residence of the LabTech
     */
    @Length ( max = 15 )
    private String  city;

    /**
     * The state of residence of the LabTech
     */
    @Enumerated ( EnumType.STRING )
    private State   state;

    /**
     * The zipcode of the LabTech
     */
    @Length ( min = 5, max = 10 )
    private String  zip;

    /**
     * The phone number of the LabTech
     */
    @Length ( min = 12, max = 12 )
    private String  phone;

    /**
     * The specialty of the LabTech
     */
    private String  specialty; /*
                                * Possibly consider making this an enum in the
                                * future
                                */

    /**
     * The email of the LabTech
     */
    @Length ( max = 30 )
    private String  email;

    /**
     * The id of the LabTech
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long    id;

    /**
     * Create a new LabTech based off of the LabTechForm
     *
     * @param form
     *            the filled-in LabTech form with LabTech information
     */
    public LabTech ( final PersonnelForm form ) {
        setSelf( User.getByName( form.getSelf() ) );
        setEnabled( form.getEnabled() != null ? 1 : 0 );
        setFirstName( form.getFirstName() );
        setLastName( form.getLastName() );
        setAddress1( form.getAddress1() );
        setAddress2( form.getAddress2() );
        setCity( form.getCity() );
        setState( State.valueOf( form.getState() ) );
        setZip( form.getZip() );
        setPhone( form.getPhone() );
        setSpecialty( form.getSpecialty() );
        setEmail( form.getEmail() );
        try {
            setId( Long.valueOf( form.getId() ) );
        }
        catch ( NullPointerException | NumberFormatException npe ) {
            /* Will not have ID set if fresh form */
        }
    }

    /**
     * Empty constructor necessary for Hibernate.
     */
    public LabTech () {

    }

    /**
     * Get the id of this LabTech
     *
     * @return the id of this LabTech
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the id of this LabTech
     *
     * @param id
     *            the id to set this LabTech to
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Get the user representation of this LabTech
     *
     * @return the user representation of this LabTech
     */
    public User getSelf () {
        return self;
    }

    /**
     * Set the user representation of this LabTech
     *
     * @param self
     *            the user representation to set this LabTech to
     */
    public void setSelf ( final User self ) {
        this.self = self;
    }

    /**
     * Get whether or not this LabTech is enabled
     *
     * @return whether or not this LabTech is enabled
     */
    public Integer getEnabled () {
        return enabled;
    }

    /**
     * Set whether or not this LabTech is enabled
     *
     * @param enabled
     *            whether or not this LabTech is enabled
     */
    public void setEnabled ( final Integer enabled ) {
        this.enabled = enabled;
    }

    /**
     * Get the first name of this LabTech
     *
     * @return the first name of this LabTech
     */
    public String getFirstName () {
        return firstName;
    }

    /**
     * Set the first name of this LabTech
     *
     * @param firstName
     *            the first name to set this LabTech to
     */
    public void setFirstName ( final String firstName ) {
        this.firstName = firstName;
    }

    /**
     * Get the last name of this LabTech
     *
     * @return the last name of this LabTech
     */
    public String getLastName () {
        return lastName;
    }

    /**
     * Set the last name of this LabTech
     *
     * @param lastName
     *            the last name to set this LabTech to
     */
    public void setLastName ( final String lastName ) {
        this.lastName = lastName;
    }

    /**
     * Get the address line 1 of this LabTech
     *
     * @return the address line 1 of this LabTech
     */
    public String getAddress1 () {
        return address1;
    }

    /**
     * Set the address line 1 of this LabTech
     *
     * @param address1
     *            the address line 1 to set this LabTech to
     */
    public void setAddress1 ( final String address1 ) {
        this.address1 = address1;
    }

    /**
     * Get the address line 2 of this LabTech
     *
     * @return the address line 2 of this LabTech
     */
    public String getAddress2 () {
        return address2;
    }

    /**
     * Set the address line 2 of this LabTech
     *
     * @param address2
     *            the address line 2 to set this LabTech to
     */
    public void setAddress2 ( final String address2 ) {
        this.address2 = address2;
    }

    /**
     * Get the city of residence of this LabTech
     *
     * @return the city of residence of this LabTech
     */
    public String getCity () {
        return city;
    }

    /**
     * Set the city of residence of this LabTech
     *
     * @param city
     *            the city of residence to set this LabTech to
     */
    public void setCity ( final String city ) {
        this.city = city;
    }

    /**
     * Get the state of residence of this LabTech
     *
     * @return the state of residence of this LabTech
     */
    public State getState () {
        return state;
    }

    /**
     * Set the state of residence of this LabTech
     *
     * @param state
     *            the state of residence to set this LabTech to
     */
    public void setState ( final State state ) {
        this.state = state;
    }

    /**
     * Get the zipcode of this LabTech
     *
     * @return the zipcode of this LabTech
     */
    public String getZip () {
        return zip;
    }

    /**
     * Set the zipcode of this LabTech
     *
     * @param zip
     *            the zipcode to set this LabTech to
     */
    public void setZip ( final String zip ) {
        this.zip = zip;
    }

    /**
     * Get the phone number of this LabTech
     *
     * @return the phone number of this LabTech
     */
    public String getPhone () {
        return phone;
    }

    /**
     * Set the phone number of this LabTech
     *
     * @param phone
     *            the phone number to set this LabTech to
     */
    public void setPhone ( final String phone ) {
        this.phone = phone;
    }

    /**
     * Get the specialty of this LabTech
     *
     * @return the specialty of this LabTech
     */
    public String getSpecialty () {
        return specialty;
    }

    /**
     * Set the specialty of this LabTech
     *
     * @param specialty
     *            the specialty to set this LabTech to
     */
    public void setSpecialty ( final String specialty ) {
        this.specialty = specialty;
    }

    /**
     * Get the email of this LabTech
     *
     * @return the email of this LabTech
     */
    public String getEmail () {
        return email;
    }

    /**
     * Set the email of this LabTech
     *
     * @param email
     *            the email to set this LabTech to
     */
    public void setEmail ( final String email ) {
        this.email = email;
    }

}
