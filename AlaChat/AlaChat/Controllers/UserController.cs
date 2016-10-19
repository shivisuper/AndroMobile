using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace AlaChat.Controllers
{
    public class UserController : ApiController
    {
        AlaChatEntities db = new AlaChatEntities();
        [HttpGet]
        // GET api/<controller>
        public IEnumerable<ala_user> Get()
        {
            return db.ala_user.ToList();
        }

        // GET api/<controller>/5
        public string Get(int id)
        {
            return "value";
        }

        // POST api/<controller>
        public void Post([FromBody]string value)
        {
        }

        // PUT api/<controller>/5
        public void Put(int id, [FromBody]string value)
        {
        }


        [ActionName("getme")]
        [HttpPost]
        public string getme(string inme) { return inme+"out"; }
        // DELETE api/<controller>/5
        public void Delete(int id)
        {
        }
    }
}