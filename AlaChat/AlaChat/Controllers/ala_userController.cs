using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Data.Entity.Infrastructure;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using System.Web.Http.ModelBinding;
using System.Web.Http.OData;
using System.Web.Http.OData.Routing;
using AlaChat;
using AlaChat.Models;

namespace AlaChat.Controllers
{
    /*
    The WebApiConfig class may require additional changes to add a route for this controller. Merge these statements into the Register method of the WebApiConfig class as applicable. Note that OData URLs are case sensitive.

    using System.Web.Http.OData.Builder;
    using System.Web.Http.OData.Extensions;
    using AlaChat;
    ODataConventionModelBuilder builder = new ODataConventionModelBuilder();
    builder.EntitySet<ala_user>("ala_user");
    config.Routes.MapODataServiceRoute("odata", "odata", builder.GetEdmModel());
    */
    public class ala_userController : ODataController
    {
        private AlaChatContext db = new AlaChatContext();

        // GET: odata/ala_user
        [EnableQuery]
        public IQueryable<ala_user> Getala_user()
        {
            return db.ala_user;
        }

        // GET: odata/ala_user(5)
        [EnableQuery]
        public SingleResult<ala_user> Getala_user([FromODataUri] int key)
        {
            return SingleResult.Create(db.ala_user.Where(ala_user => ala_user.Id == key));
        }

        // PUT: odata/ala_user(5)
        public IHttpActionResult Put([FromODataUri] int key, Delta<ala_user> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            ala_user ala_user = db.ala_user.Find(key);
            if (ala_user == null)
            {
                return NotFound();
            }

            patch.Put(ala_user);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!ala_userExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(ala_user);
        }

        // POST: odata/ala_user
        public IHttpActionResult Post(ala_user ala_user)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.ala_user.Add(ala_user);
            db.SaveChanges();

            return Created(ala_user);
        }

        // PATCH: odata/ala_user(5)
        [AcceptVerbs("PATCH", "MERGE")]
        public IHttpActionResult Patch([FromODataUri] int key, Delta<ala_user> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            ala_user ala_user = db.ala_user.Find(key);
            if (ala_user == null)
            {
                return NotFound();
            }

            patch.Patch(ala_user);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!ala_userExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(ala_user);
        }

        // DELETE: odata/ala_user(5)
        public IHttpActionResult Delete([FromODataUri] int key)
        {
            ala_user ala_user = db.ala_user.Find(key);
            if (ala_user == null)
            {
                return NotFound();
            }

            db.ala_user.Remove(ala_user);
            db.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool ala_userExists(int key)
        {
            return db.ala_user.Count(e => e.Id == key) > 0;
        }
    }
}
