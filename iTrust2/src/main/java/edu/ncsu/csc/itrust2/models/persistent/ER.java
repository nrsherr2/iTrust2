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

//import edu.ncsu.csc.itrust2.forms.ER.ERForm;
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
@Table ( name = "ER" )
public class ER extends DomainObject<ER> {

    /**
     * Get the ER by username
     *
     * @param username
     *            the username of the ER to get
     * @return the ER result with the queried username
     */
    public static ER getByName ( final String username ) {
        return getByName( User.getByName( username ) );
    }

    /**
     * Get the ER by username, done by passing the User representation of
     * the ER
     *
     * @param self
     *            the self the user representation of the ER with their
     *            username
     * @return the ER result with the queried username
     */
    public static ER getByName ( final User self ) {
        try {
            return getWhere( createCriterionAsList( "self", self ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Get all ER in the DB
     *
     * @SuppressWarnings for Unchecked cast from List<capture#1-of ? extends
     *                   DomainObject> to List<ER> Because get all just
     *                   returns a list of DomainObjects, the cast is okay.
     *
     * @return all ER in the DB
     */
    @SuppressWarnings ( "unchecked" )
    static public List<ER> getER () {
        return (List<ER>) getAll( ER.class );
    }

    /**
     * Get all ER in the database where the passed query is true
     *
     * @SuppressWarnings for Unchecked cast from List<capture#1-of ? extends
     *                   DomainObject> to List<ER> Because get all just
     *                   returns a list of DomainObjects, the cast is okay.
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return all ER in the database where the passed query is true
     */
    @SuppressWarnings ( "unchecked" )
    private static List<ER> getWhere ( final List<Criterion> where ) {
        return (List<ER>) getWhere( ER.class, where );
    }

    /**
     * This stores a reference to the User object that this ER is.
     * Mandatory.
     */
    @JoinColumn ( name = "self_id", columnDefinition = "varchar(100)" )
    @OneToOne
    private User    self;

    /**
     * Whether or not the ER is enabled
     */
    private Integer enabled;

    /**
     * The first name of the ER
     */
    @Length ( max = 20 )
    private String  firstName;

    /**
     * The last name of the ER
     */
    @Length ( max = 30 )
    private String  lastName;

    /**
     * The address line 1 of the ER
     */
    @Length ( max = 50 )
    private String  address1;

    /**
     * The address line 2 of the ER
     */
    @Length ( max = 50 )
    private String  address2;

    /**
     * The city of residence of the ER
     */
    @Length ( max = 15 )
    private String  city;

    /**
     * The state of residence of the ER
     */
    @Enumerated ( EnumType.STRING )
    private State   state;

    /**
     * The zipcode of the ER
     */
    @Length ( min = 5, max = 10 )
    private String  zip;

    /**
     * The phone number of the ER
     */
    @Length ( min = 12, max = 12 )
    private String  phone;

    /**
     * The specialty of the ER
     */
    private String  specialty; /*
                                * Possibly consider making this an enum in the
                                * future
                                */

    /**
     * The email of the ER
     */
    @Length ( max = 30 )
    private String  email;

    /**
     * The id of the ER
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long    id;

    /**
     * Create a new ER based off of the ERForm
     *
     * @param form
     *            the filled-in ER form with ER information
     */
    public ER ( final PersonnelForm form ) {
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
    public ER () {

    }

    /**
     * Get the id of this ER
     *
     * @return the id of this ER
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the id of this ER
     *
     * @param id
     *            the id to set this ER to
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Get the user representation of this ER
     *
     * @return the user representation of this ER
     */
    public User getSelf () {
        return self;
    }

    /**
     * Set the user representation of this ER
     *
     * @param self
     *            the user representation to set this ER to
     */
    public void setSelf ( final User self ) {
        this.self = self;
    }

    /**
     * Get whether or not this ER is enabled
     *
     * @return whether or not this ER is enabled
     */
    public Integer getEnabled () {
        return enabled;
    }

    /**
     * Set whether or not this ER is enabled
     *
     * @param enabled
     *            whether or not this ER is enabled
     */
    public void setEnabled ( final Integer enabled ) {
        this.enabled = enabled;
    }

    /**
     * Get the first name of this ER
     *
     * @return the first name of this ER
     */
    public String getFirstName () {
        return firstName;
    }

    /**
     * Set the first name of this ER
     *
     * @param firstName
     *            the first name to set this ER to
     */
    public void setFirstName ( final String firstName ) {
        this.firstName = firstName;
    }

    /**
     * Get the last name of this ER
     *
     * @return the last name of this ER
     */
    public String getLastName () {
        return lastName;
    }

    /**
     * Set the last name of this ER
     *
     * @param lastName
     *            the last name to set this ER to
     */
    public void setLastName ( final String lastName ) {
        this.lastName = lastName;
    }

    /**
     * Get the address line 1 of this ER
     *
     * @return the address line 1 of this ER
     */
    public String getAddress1 () {
        return address1;
    }

    /**
     * Set the address line 1 of this ER
     *
     * @param address1
     *            the address line 1 to set this ER to
     */
    public void setAddress1 ( final String address1 ) {
        this.address1 = address1;
    }

    /**
     * Get the address line 2 of this ER
     *
     * @return the address line 2 of this ER
     */
    public String getAddress2 () {
        return address2;
    }

    /**
     * Set the address line 2 of this ER
     *
     * @param address2
     *            the address line 2 to set this ER to
     */
    public void setAddress2 ( final String address2 ) {
        this.address2 = address2;
    }

    /**
     * Get the city of residence of this ER
     *
     * @return the city of residence of this ER
     */
    public String getCity () {
        return city;
    }

    /**
     * Set the city of residence of this ER
     *
     * @param city
     *            the city of residence to set this ER to
     */
    public void setCity ( final String city ) {
        this.city = city;
    }

    /**
     * Get the state of residence of this ER
     *
     * @return the state of residence of this ER
     */
    public State getState () {
        return state;
    }

    /**
     * Set the state of residence of this ER
     *
     * @param state
     *            the state of residence to set this ER to
     */
    public void setState ( final State state ) {
        this.state = state;
    }

    /**
     * Get the zipcode of this ER
     *
     * @return the zipcode of this ER
     */
    public String getZip () {
        return zip;
    }

    /**
     * Set the zipcode of this ER
     *
     * @param zip
     *            the zipcode to set this ER to
     */
    public void setZip ( final String zip ) {
        this.zip = zip;
    }

    /**
     * Get the phone number of this ER
     *
     * @return the phone number of this ER
     */
    public String getPhone () {
        return phone;
    }

    /**
     * Set the phone number of this ER
     *
     * @param phone
     *            the phone number to set this ER to
     */
    public void setPhone ( final String phone ) {
        this.phone = phone;
    }

    /**
     * Get the specialty of this ER
     *
     * @return the specialty of this ER
     */
    public String getSpecialty () {
        return specialty;
    }

    /**
     * Set the specialty of this ER
     *
     * @param specialty
     *            the specialty to set this ER to
     */
    public void setSpecialty ( final String specialty ) {
        this.specialty = specialty;
    }

    /**
     * Get the email of this ER
     *
     * @return the email of this ER
     */
    public String getEmail () {
        return email;
    }

    /**
     * Set the email of this ER
     *
     * @param email
     *            the email to set this ER to
     */
    public void setEmail ( final String email ) {
        this.email = email;
    }

}
