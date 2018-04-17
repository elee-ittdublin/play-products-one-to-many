package controllers;

import play.mvc.*;
import play.data.*;
import play.db.ebean.Transactional;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import models.*;

import views.html.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

        /** Dependency Injection **/

    /** http://stackoverflow.com/questions/15600186/play-framework-dependency-injection **/
    private FormFactory formFactory;

    /** http://stackoverflow.com/a/37024198 **/
    //private Environment env;

    /** http://stackoverflow.com/a/10159220/6322856 **/
    @Inject
    public HomeController(/*Environment e,*/ FormFactory f) {
        //this.env = e;
        this.formFactory = f;
    }


    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        return ok(index.render("Hello World!"));
    }

    public Result about() {
        return ok(about.render());
    }

    @Transactional
    public Result products(Long cat) {

        // Find the products (in the DB) and add to a Product arrayList
        // Calls the find.all() method of Product - from the Model superclass
        // https://www.playframework.com/documentation/2.6.x/JavaEbean#Using-Model-superclass
        List<Product> productList = new ArrayList<Product>();
        List<Category> categoryList = Category.find.all();

        if (cat == 0) {
            productList = Product.find.all();
        }
        else {
            // Get products for selected category
            // Find category then extract products
            productList = Category.find.ref(cat).getProducts();
        }

        // Return the view, passing the product list as a parameter
        return ok(products.render(productList, categoryList));
    }

        // Load the add product view
    // Display an empty form in the view
    @Transactional
    public Result addProduct() {   
        // Instantiate a form object based on the Product class
        Form<Product> addProductForm = formFactory.form(Product.class);
        // Render the Add Product View, passing the form object
        return ok(addProduct.render(addProductForm));
    }

    @Transactional
    public Result addProductSubmit() {

        // Create a product form object (to hold submitted data)
        // 'Bind' the object to the submitted form (this copies the filled form)
        Form<Product> newProductForm = formFactory.form(Product.class).bindFromRequest();

        // Check for errors (based on Product class annotations)	
        if(newProductForm.hasErrors()) {
            // Display the form again
            return badRequest(addProduct.render(newProductForm));
        }
        
        // Save the Product using Ebean (remember Product extends Model)
        newProductForm.get().save();

        // Set a flash message
        flash("success", "Product " + newProductForm.get().getName() + " has been created");
        
        // Redirect to the admin home
        return redirect(controllers.routes.HomeController.products(0));
    }


}